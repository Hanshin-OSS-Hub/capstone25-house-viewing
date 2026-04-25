package com.capstone.houseviewingapp.analysis

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.analysis.model.ApiRiskLevel
import com.capstone.houseviewingapp.analysis.model.PreContractDiagnosisRequest
import com.capstone.houseviewingapp.data.local.AuthTokenLocalStore
import com.capstone.houseviewingapp.data.local.BillingLocalStore
import com.capstone.houseviewingapp.data.local.QuickDiagnosisLocalStore
import com.capstone.houseviewingapp.data.remote.ApiErrorFormatter
import com.capstone.houseviewingapp.data.remote.RemoteApiException
import com.capstone.houseviewingapp.databinding.FragmentAnalysisLoadingBinding
import com.capstone.houseviewingapp.subscription.SubscriptionRepositoryProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnalysisLoadingFragment : Fragment() {
    private var _binding: FragmentAnalysisLoadingBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler(Looper.getMainLooper())
    private var currentStep = 0
    private var analysisJob: Job? = null
    private var visualTickerStartedAt = 0L
    private var isAnalysisDone = false
    private var completionPayload: CompletionPayload? = null

    private val stepPulseAnimators = mutableMapOf<Int, AnimatorSet>()

    companion object {
        private const val STATUS_PENDING = "Pending"
        private const val STATUS_PROCESSING = "Processing..."
        private const val STATUS_COMPLETED = "Completed"
    }

    private data class CompletionPayload(
        val source: RecordSource,
        val title: String,
        val address: String,
        val riskSummary: String,
        val level: RiskLevel,
        val ltvScore: Double?,
        val sourcePdfUri: String?
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCancel.setOnClickListener {
            analysisJob?.cancel()
            findNavController().popBackStack()
        }

        startCenterLoadingAnimation()
        updateStepUi()
        startVisualTicker()
        startAnalysisRequest()
    }

    private fun startVisualTicker() {
        visualTickerStartedAt = System.currentTimeMillis()
        handler.post(object : Runnable {
            override fun run() {
                if (isAnalysisDone || _binding == null) return
                val elapsedSec = ((System.currentTimeMillis() - visualTickerStartedAt) / 1000L).toInt()
                val targetStep = when {
                    elapsedSec >= 9 -> 3
                    elapsedSec >= 6 -> 2
                    elapsedSec >= 3 -> 1
                    else -> 0
                }
                if (targetStep != currentStep) {
                    currentStep = targetStep
                    updateStepUi()
                }
                handler.postDelayed(this, 300L)
            }
        })
    }

    private fun startAnalysisRequest() {
        analysisJob = viewLifecycleOwner.lifecycleScope.launch {
            val sourceRaw = arguments?.getString(AnalysisFlow.ARG_ANALYSIS_SOURCE)
            val source = if (sourceRaw == AnalysisFlow.SOURCE_AUTO) RecordSource.AUTO else RecordSource.MANUAL

            val houses = com.capstone.houseviewingapp.data.local.HouseLocalStore.getHouses(requireContext())
            val requestedHouseId = arguments?.getLong(AnalysisFlow.ARG_HOUSE_ID, -1L) ?: -1L
            val primaryHouse = when {
                requestedHouseId > 0L -> {
                    houses.firstOrNull { it.houseId == requestedHouseId }
                        ?: houses.firstOrNull { (it.houseId ?: -1L) > 0L }
                }
                else -> houses.firstOrNull { (it.houseId ?: -1L) > 0L }
            }
            val manualAddress = arguments?.getString(AnalysisFlow.ARG_ORIGIN_ADDRESS)
                ?: primaryHouse?.address
                ?: "주소 수신 대기"
            val manualTitle = arguments?.getString(AnalysisFlow.ARG_HOUSE_NICKNAME)
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: "무료 1회 진단"
            val accessToken = AuthTokenLocalStore.getAccessToken(requireContext()).orEmpty()
            if (accessToken.isBlank()) {
                Toast.makeText(requireContext(), "로그인 정보가 만료되었습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return@launch
            }

            val apiResult = when (source) {
                RecordSource.MANUAL -> {
                    requestManualDiagnosis(
                        accessToken = accessToken,
                        fileUri = arguments?.getString(AnalysisFlow.ARG_SELECTED_FILE_URI).orEmpty(),
                        manualTitle = manualTitle,
                        manualAddress = manualAddress
                    )
                }
                RecordSource.AUTO -> {
                    val houseId = primaryHouse?.houseId
                    if (houseId == null || houseId <= 0L) {
                        Toast.makeText(requireContext(), "자동 분석할 집 정보가 없습니다. 집 등록 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                        return@launch
                    }
                    AnalysisRepositoryProvider.repository.changeDiagnoses(accessToken, houseId)
                }
            }

            val pdf = apiResult.getOrElse { throwable ->
                if (source == RecordSource.AUTO) {
                    val fallbackPayload = buildAutoFallbackPayload(
                        accessToken = accessToken,
                        primaryHouse = primaryHouse
                    )
                    if (fallbackPayload != null) {
                        completionPayload = fallbackPayload
                        isAnalysisDone = true
                        currentStep = 4
                        updateStepUi()
                        playCheckAppearAnimation(iconForStep(4))
                        delay(300L)
                        if (_binding != null) showCompleteDialog()
                        return@launch
                    }
                }
                val remote = throwable as? RemoteApiException
                if (remote?.code == "AU005" && source == RecordSource.MANUAL) {
                    val loginId = AuthTokenLocalStore.getLoginId(requireContext()).orEmpty()
                    QuickDiagnosisLocalStore.markFreeUsed(requireContext(), loginId)
                }
                Toast.makeText(requireContext(), mapAnalysisErrorMessage(throwable, source), Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
                return@launch
            }
            if (source == RecordSource.MANUAL) {
                val loginId = AuthTokenLocalStore.getLoginId(requireContext()).orEmpty()
                QuickDiagnosisLocalStore.markFreeUsed(requireContext(), loginId)
            }

            val resolvedTitle = if (source == RecordSource.AUTO) {
                primaryHouse?.homeName ?: "자동 감지 분석"
            } else {
                manualTitle
            }
            val resolvedAddress = if (source == RecordSource.AUTO) {
                primaryHouse?.address ?: "등록된 집 정보 없음"
            } else {
                manualAddress
            }
            val latestMeta = fetchLatestAnalysisMetaWithRetry(
                accessToken = accessToken,
                nickname = resolvedTitle,
                address = resolvedAddress,
                source = source
            )
            completionPayload = CompletionPayload(
                source = source,
                title = resolvedTitle,
                address = resolvedAddress,
                riskSummary = latestMeta?.mainReason?.takeIf { it.isNotBlank() }
                    ?: "상세 리포트에서 주요 원인을 확인해 주세요.",
                level = latestMeta?.riskLevel?.toUiRiskLevel()
                    ?: RiskLevel.AMBER,
                ltvScore = latestMeta?.ltvScore?.toDouble(),
                // 분석 결과 카드의 상세 리포트는 생성된 결과 PDF를 우선 사용
                sourcePdfUri = pdf.filePath.takeIf { it.isNotBlank() }
                    ?: arguments?.getString(AnalysisFlow.ARG_SELECTED_FILE_URI)?.trim()?.ifBlank { null }
            )

            isAnalysisDone = true
            currentStep = 4
            updateStepUi()
            playCheckAppearAnimation(iconForStep(4))
            delay(500L)
            if (_binding != null) showCompleteDialog()
        }
    }

    private fun showCompleteDialog() {
        val payload = completionPayload ?: run {
            Toast.makeText(requireContext(), "분석 결과가 없습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        parentFragmentManager.setFragmentResultListener(
            AnalysisCompleteDialogFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, _ ->
            val record = AnalysisRecordItem(
                title = payload.title,
                address = payload.address,
                riskSummary = payload.riskSummary,
                level = payload.level,
                source = payload.source,
                ltv = payload.ltvScore,
                sourcePdfUri = payload.sourcePdfUri
            )
            com.capstone.houseviewingapp.data.local.AnalysisLocalStore.addRecord(requireContext(), record)
            val navController = findNavController()
            val options = androidx.navigation.navOptions {
                popUpTo(R.id.nav_analysis_loading) { inclusive = true }
                launchSingleTop = true
            }
            navController.navigate(R.id.nav_analysis, null, options)

            val bottomNav = requireActivity()
                .findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    R.id.navigationBar
                )
            bottomNav.post {
                bottomNav.menu.findItem(R.id.nav_analysis).isChecked = true
            }
        }
        AnalysisCompleteDialogFragment().show(parentFragmentManager, "AnalysisCompleteDialog")
    }

    private suspend fun requestManualDiagnosis(
        accessToken: String,
        fileUri: String,
        manualTitle: String,
        manualAddress: String
    ): Result<com.capstone.houseviewingapp.analysis.model.PdfDownloadResponse> {
        val initialResult = AnalysisRepositoryProvider.repository.preContractDiagnoses(
            context = requireContext(),
            accessToken = accessToken,
            fileUri = fileUri,
            request = PreContractDiagnosisRequest(
                nickname = manualTitle,
                address = manualAddress
            )
        )
        val remote = initialResult.exceptionOrNull() as? RemoteApiException
        val isPremium = BillingLocalStore.isPremium(requireContext())
        if (remote?.code != "AU005" || !isPremium) return initialResult

        val subscribeResult = SubscriptionRepositoryProvider.repository.subscribePremium(accessToken)
        if (subscribeResult.isFailure) return initialResult

        return AnalysisRepositoryProvider.repository.preContractDiagnoses(
            context = requireContext(),
            accessToken = accessToken,
            fileUri = fileUri,
            request = PreContractDiagnosisRequest(
                nickname = manualTitle,
                address = manualAddress
            )
        )
    }

    private fun mapAnalysisErrorMessage(throwable: Throwable, source: RecordSource): String {
        val remote = throwable as? RemoteApiException
        if (remote?.statusCode == 500 && (remote.code.isNullOrBlank() || remote.code == "UNKNOWN")) {
            return "분석 엔진(PDF 생성) 처리 중 오류가 발생했습니다. 더미 PDF가 아닌 실제 등기부등본으로 다시 시도해 주세요."
        }
        if (remote?.statusCode == 404) {
            return "분석 API 경로를 찾지 못했습니다(HTTP 404). 서버 재시작 후에도 계속 발생하면 서버 라우트 버전 불일치입니다."
        }
        return when (remote?.code) {
            "AU001", "AU002", "AU003", "AU005", "AU006" ->
                if (remote.code == "AU005") {
                    if (source == RecordSource.AUTO) {
                        "자동 감지 분석은 무료 진단 소진으로 제한됩니다. 진단하기 버튼에서 유료 진단을 진행해 주세요. (코드: AU005)"
                    } else {
                        "무료 1회 진단을 이미 사용했습니다. 결제 후 다시 시도해 주세요. (코드: AU005)"
                    }
                } else {
                    "인증이 만료되었습니다. 다시 로그인해 주세요. (코드: ${remote.code})"
                }
            "ER002" ->
                "분석 엔진 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요. (코드: ER002)"
            "NF001", "NF002" ->
                "등록된 집/주소를 찾지 못했습니다. 집 정보를 다시 확인해 주세요. (코드: ${remote.code})"
            else -> ApiErrorFormatter.withCode("분석 요청에 실패했습니다.", throwable)
        }
    }

    private suspend fun fetchLatestAnalysisMetaWithRetry(
        accessToken: String,
        nickname: String,
        address: String,
        source: RecordSource
    ): com.capstone.houseviewingapp.analysis.model.AnalysisResponse? {
        repeat(4) { attempt ->
            val analyses = AnalysisRepositoryProvider.repository
                .getAnalyses(accessToken)
                .getOrNull()
                .orEmpty()
            val diffAnalyses = AnalysisRepositoryProvider.repository
                .getDiffAnalyses(accessToken)
                .getOrNull()
                .orEmpty()
            val candidates = when (source) {
                RecordSource.AUTO -> diffAnalyses + analyses
                RecordSource.MANUAL -> analyses + diffAnalyses
            }

            val best = candidates
                .asSequence()
                .sortedWith(
                    compareByDescending<com.capstone.houseviewingapp.analysis.model.AnalysisResponse> {
                        it.nickname == nickname
                    }.thenByDescending {
                        matchAddressScore(it.address, address)
                    }.thenByDescending {
                        // 주요 원인을 우선 확보하도록 가중치 부여
                        it.mainReason?.isNotBlank() == true
                    }.thenByDescending {
                        it.riskLevel != null
                    }.thenByDescending {
                        it.ltvScore != null
                    }
                )
                .firstOrNull()
            if (best != null && (best.ltvScore != null || !best.mainReason.isNullOrBlank() || best.riskLevel != null)) {
                return best
            }
            if (attempt < 3) delay(700L)
        }
        return null
    }

    private fun matchAddressScore(serverAddress: String, localAddress: String): Int {
        val a = normalizeAddress(serverAddress)
        val b = normalizeAddress(localAddress)
        if (a.isBlank() || b.isBlank()) return 0
        return when {
            a == b -> 3
            a.contains(b) || b.contains(a) -> 2
            else -> 0
        }
    }

    private fun normalizeAddress(value: String): String {
        return value
            .lowercase()
            .replace(Regex("\\s+"), "")
            .replace("대한민국", "")
            .replace("경기도", "경기")
    }

    private suspend fun buildAutoFallbackPayload(
        accessToken: String,
        primaryHouse: com.capstone.houseviewingapp.home.HouseCardItem?
    ): CompletionPayload? {
        val houseName = primaryHouse?.homeName.orEmpty()
        val houseAddress = primaryHouse?.address.orEmpty()
        val latest = AnalysisRepositoryProvider.repository
            .getDiffAnalyses(accessToken)
            .getOrNull()
            .orEmpty()
            .asSequence()
            .sortedWith(
                compareByDescending<com.capstone.houseviewingapp.analysis.model.AnalysisResponse> {
                    it.nickname == houseName
                }.thenByDescending {
                    matchAddressScore(it.address, houseAddress)
                }.thenByDescending {
                    it.mainReason?.isNotBlank() == true
                }.thenByDescending {
                    it.ltvScore != null
                }
            )
            .firstOrNull()
            ?: return null

        return CompletionPayload(
            source = RecordSource.AUTO,
            title = primaryHouse?.homeName ?: latest.nickname,
            address = primaryHouse?.address ?: latest.address,
            riskSummary = latest.mainReason?.takeIf { it.isNotBlank() }
                ?: "최근 자동 감지 분석 결과를 불러왔습니다.",
            level = latest.riskLevel?.toUiRiskLevel() ?: RiskLevel.AMBER,
            ltvScore = latest.ltvScore?.toDouble(),
            sourcePdfUri = null
        )
    }

    private fun ApiRiskLevel.toUiRiskLevel(): RiskLevel = when (this) {
        ApiRiskLevel.DANGER -> RiskLevel.RED
        ApiRiskLevel.WARNING -> RiskLevel.AMBER
        ApiRiskLevel.SAFE -> RiskLevel.BLUE
    }

    private fun iconForStep(step: Int): View = when (step) {
        1 -> binding.step1Icon
        2 -> binding.step2Icon
        3 -> binding.step3Icon
        4 -> binding.step4Icon
        else -> binding.step1Icon
    }

    private fun pulseViewForStep(step: Int): View = when (step) {
        1 -> binding.step1PulseRing
        2 -> binding.step2PulseRing
        3 -> binding.step3PulseRing
        4 -> binding.step4PulseRing
        else -> binding.step1PulseRing
    }

    private fun updateStepUi() {
        val ctx = requireContext()
        val blue2 = ctx.getColor(R.color.blue2)
        val black = ctx.getColor(R.color.black)
        val darkgray = ctx.getColor(R.color.darkgray)
        val textgray = ctx.getColor(R.color.textgray)
        val icongray = ctx.getColor(R.color.icongray)

        // 진행 중 단계 파장만 켜기
        stopAllStepPulses()
        when (currentStep) {
            0 -> startStepPulse(1)
            1 -> startStepPulse(2)
            2 -> startStepPulse(3)
            3 -> startStepPulse(4)
        }

        // 1단계
        if (currentStep >= 1) {
            binding.step1Text.text = "등본 데이터 스캔 완료"
            binding.step1Text.setTextColor(black)
            binding.step1StatusText.text = STATUS_COMPLETED
            binding.step1StatusText.setTextColor(darkgray)
            binding.step1Icon.setImageResource(R.drawable.round_check_circle_24)
            binding.step1Icon.setColorFilter(blue2)
            resetIconTransform(binding.step1Icon)
        } else {
            binding.step1Text.text = "등본 데이터 스캔 중"
            binding.step1Text.setTextColor(blue2)
            binding.step1StatusText.text = STATUS_PROCESSING
            binding.step1StatusText.setTextColor(blue2)
            binding.step1Icon.setImageResource(R.drawable.outline_query_stats_24)
            binding.step1Icon.setColorFilter(blue2)
            resetIconTransform(binding.step1Icon)
        }

        // 2단계
        if (currentStep >= 2) {
            binding.step2Text.text = "권리 관계 및 LTV 계산 완료"
            binding.step2Text.setTextColor(black)
            binding.step2StatusText.text = STATUS_COMPLETED
            binding.step2StatusText.setTextColor(darkgray)
            binding.step2Icon.setImageResource(R.drawable.round_check_circle_24)
            binding.step2Icon.setColorFilter(blue2)
            resetIconTransform(binding.step2Icon)
        } else if (currentStep == 1) {
            binding.step2Text.text = "권리 관계 및 LTV 계산 중"
            binding.step2Text.setTextColor(blue2)
            binding.step2StatusText.text = STATUS_PROCESSING
            binding.step2StatusText.setTextColor(blue2)
            binding.step2Icon.setImageResource(R.drawable.outline_analytics_24)
            binding.step2Icon.setColorFilter(blue2)
            resetIconTransform(binding.step2Icon)
        } else {
            binding.step2Text.text = "권리 관계 및 LTV 계산"
            binding.step2Text.setTextColor(darkgray)
            binding.step2StatusText.text = STATUS_PENDING
            binding.step2StatusText.setTextColor(textgray)
            binding.step2Icon.setImageResource(R.drawable.outline_analytics_24)
            binding.step2Icon.setColorFilter(icongray)
            resetIconTransform(binding.step2Icon)
        }

        // 3단계
        if (currentStep >= 3) {
            binding.step3Text.text = "회수 금액 및 위험도 평가 완료"
            binding.step3Text.setTextColor(black)
            binding.step3StatusText.text = STATUS_COMPLETED
            binding.step3StatusText.setTextColor(darkgray)
            binding.step3Icon.setImageResource(R.drawable.round_check_circle_24)
            binding.step3Icon.setColorFilter(blue2)
            resetIconTransform(binding.step3Icon)
        } else if (currentStep == 2) {
            binding.step3Text.text = "회수 금액 및 위험도 평가 중"
            binding.step3Text.setTextColor(blue2)
            binding.step3StatusText.text = STATUS_PROCESSING
            binding.step3StatusText.setTextColor(blue2)
            binding.step3Icon.setImageResource(R.drawable.outline_payments_24)
            binding.step3Icon.setColorFilter(blue2)
            resetIconTransform(binding.step3Icon)
        } else {
            binding.step3Text.text = "회수 금액 및 위험도 평가"
            binding.step3Text.setTextColor(darkgray)
            binding.step3StatusText.text = STATUS_PENDING
            binding.step3StatusText.setTextColor(textgray)
            binding.step3Icon.setImageResource(R.drawable.outline_payments_24)
            binding.step3Icon.setColorFilter(icongray)
            resetIconTransform(binding.step3Icon)
        }

        // 4단계
        if (currentStep >= 4) {
            binding.step4Text.text = "분석 리포트 PDF 생성 완료"
            binding.step4Text.setTextColor(black)
            binding.step4StatusText.text = STATUS_COMPLETED
            binding.step4StatusText.setTextColor(darkgray)
            binding.step4Icon.setImageResource(R.drawable.round_check_circle_24)
            binding.step4Icon.setColorFilter(blue2)
            resetIconTransform(binding.step4Icon)
        } else if (currentStep == 3) {
            binding.step4Text.text = "분석 리포트 PDF 생성 중"
            binding.step4Text.setTextColor(blue2)
            binding.step4StatusText.text = STATUS_PROCESSING
            binding.step4StatusText.setTextColor(blue2)
            binding.step4Icon.setImageResource(R.drawable.outline_description_24)
            binding.step4Icon.setColorFilter(blue2)
            resetIconTransform(binding.step4Icon)
        } else {
            binding.step4Text.text = "분석 리포트 PDF 생성"
            binding.step4Text.setTextColor(darkgray)
            binding.step4StatusText.text = STATUS_PENDING
            binding.step4StatusText.setTextColor(textgray)
            binding.step4Icon.setImageResource(R.drawable.outline_description_24)
            binding.step4Icon.setColorFilter(icongray)
            resetIconTransform(binding.step4Icon)
        }
    }

    private fun resetIconTransform(icon: View) {
        icon.rotation = 0f
        icon.scaleX = 1f
        icon.scaleY = 1f
        icon.alpha = 1f
    }

    private fun startStepPulse(step: Int) {
        val pulse = pulseViewForStep(step)
        pulse.visibility = View.VISIBLE
        pulse.scaleX = 0.75f
        pulse.scaleY = 0.75f
        pulse.alpha = 0.75f

        val sx = ObjectAnimator.ofFloat(pulse, View.SCALE_X, 0.75f, 1.0f).apply {
            duration = 850
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }
        val sy = ObjectAnimator.ofFloat(pulse, View.SCALE_Y, 0.75f, 1.0f).apply {
            duration = 850
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }
        val a = ObjectAnimator.ofFloat(pulse, View.ALPHA, 0.75f, 0f).apply {
            duration = 850
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }

        val set = AnimatorSet().apply {
            playTogether(sx, sy, a)
            start()
        }
        stepPulseAnimators[step] = set
    }

    private fun stopStepPulse(step: Int) {
        stepPulseAnimators[step]?.cancel()
        stepPulseAnimators.remove(step)
        val pulse = pulseViewForStep(step)
        pulse.visibility = View.GONE
        pulse.alpha = 0f
        pulse.scaleX = 1f
        pulse.scaleY = 1f
    }

    private fun stopAllStepPulses() {
        stopStepPulse(1)
        stopStepPulse(2)
        stopStepPulse(3)
        stopStepPulse(4)
    }

    private fun playCheckAppearAnimation(icon: View) {
        icon.animate().cancel()
        val scaleX = ObjectAnimator.ofFloat(icon, View.SCALE_X, 0.8f, 1.15f, 1f).apply {
            duration = 260
            interpolator = AccelerateDecelerateInterpolator()
        }
        val scaleY = ObjectAnimator.ofFloat(icon, View.SCALE_Y, 0.8f, 1.15f, 1f).apply {
            duration = 260
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            start()
        }
    }

    // 중앙 점선 링 파장(회전 없음)
    private fun startCenterLoadingAnimation() {
        val ring1 = binding.pulseRing1
        val ring2 = binding.pulseRing2
        val d = 1200L

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(ring1, View.SCALE_X, 0.85f, 1.2f).apply {
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                    duration = d
                },
                ObjectAnimator.ofFloat(ring1, View.SCALE_Y, 0.85f, 1.2f).apply {
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                    duration = d
                },
                ObjectAnimator.ofFloat(ring1, View.ALPHA, 0.5f, 1f).apply {
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                    duration = d
                }
            )
            start()
        }

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(ring2, View.SCALE_X, 0.9f, 1.15f).apply {
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                    duration = d
                    startDelay = 200
                },
                ObjectAnimator.ofFloat(ring2, View.SCALE_Y, 0.9f, 1.15f).apply {
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                    duration = d
                    startDelay = 200
                },
                ObjectAnimator.ofFloat(ring2, View.ALPHA, 0.3f, 0.8f).apply {
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                    duration = d
                    startDelay = 200
                }
            )
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        analysisJob?.cancel()
        handler.removeCallbacksAndMessages(null)
        stopAllStepPulses()
        _binding = null
    }
}