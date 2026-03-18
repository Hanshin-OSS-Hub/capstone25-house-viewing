package com.capstone.houseviewingapp.analysis

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.FragmentAnalysisLoadingBinding

class AnalysisLoadingFragment : Fragment() {
    private var _binding: FragmentAnalysisLoadingBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler(Looper.getMainLooper())
    private var currentStep = 0

    private val stepPulseAnimators = mutableMapOf<Int, AnimatorSet>()

    companion object {
        private const val STATUS_PENDING = "Pending"
        private const val STATUS_PROCESSING = "Processing..."
        private const val STATUS_COMPLETED = "Completed"
    }

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
            findNavController().popBackStack()
        }

        startCenterLoadingAnimation()
        updateStepUi()

        handler.postDelayed({ scheduleNextStep() }, 3000)
    }

    private fun scheduleNextStep() {
        if (currentStep >= 4) {
            showCompleteDialog()
            return
        }
        currentStep++
        updateStepUi()
        playCheckAppearAnimation(iconForStep(currentStep))

        // TODO: 실제 백엔드 연동 시, 각 단계별 완료 신호를 받아서 scheduleNextStep을 호출하도록 변경
        // TODO: 지금은 시뮬레이션을 위해 3초마다 다음 단계로 넘어가도록 설정, 4단계는 1초
        if (currentStep < 4) {
            handler.postDelayed({ scheduleNextStep() }, 3000)
        } else {
            handler.postDelayed({ showCompleteDialog() }, 1000)
        }
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

    private fun showCompleteDialog() {
        parentFragmentManager.setFragmentResultListener(
            AnalysisCompleteDialogFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, _ ->
            val sourceRaw = arguments?.getString(AnalysisFlow.ARG_ANALYSIS_SOURCE)
            val source = if (sourceRaw == AnalysisFlow.SOURCE_AUTO) {
                RecordSource.AUTO
            } else {
                RecordSource.MANUAL
            }

            val houses = com.capstone.houseviewingapp.data.local.HouseLocalStore.getHouses(requireContext())
            val primaryHouse = houses.firstOrNull()
            val level = when (source) {
                RecordSource.AUTO -> RiskLevel.RED
                RecordSource.MANUAL -> RiskLevel.AMBER
            }

            val manualTitle = arguments?.getString(AnalysisFlow.ARG_HOUSE_NICKNAME)?.trim()?.takeIf { it.isNotBlank() }
                ?: "무료 1회 진단"
            val record = AnalysisRecordItem(
                title = when (source) {
                    RecordSource.AUTO ->
                        primaryHouse?.homeName ?: "자동 감지 분석"
                    RecordSource.MANUAL ->
                        manualTitle
                },
                address = when (source) {
                    RecordSource.AUTO ->
                        primaryHouse?.address ?: "등록된 집 정보 없음"
                    RecordSource.MANUAL ->
                        "주소 수신 대기"
                },
                riskSummary = "분석 결과 수신 대기",
                level = level, //TODO: 백엔드에서 실제값 매핑
                source = source,
                ltv = null
            )
            // TODO(backend): 실제 API 응답값으로 title/address/riskSummary/level/ltv 매핑
            com.capstone.houseviewingapp.data.local.AnalysisLocalStore.addRecord(requireContext(), record)
            val navController = findNavController()
            val options = androidx.navigation.navOptions {
                popUpTo(R.id.nav_analysis_loading) { inclusive = true }
                launchSingleTop = true
            }
            navController.navigate(R.id.nav_analysis, null, options)

            // NOTE: 분석 화면으로 이동한 직후 하단바 선택 상태를 분석으로 강제 동기화
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

    // TODO: 백엔드와 연동 시, 각 단계별 실제 상태에 따라 start/stopStepPulse를 호출하도록 변경
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
        handler.removeCallbacksAndMessages(null)
        stopAllStepPulses()
        _binding = null
    }
}