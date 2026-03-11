package com.capstone.houseviewingapp.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.houseviewingapp.MainActivity
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.data.local.HouseLocalStore
import com.capstone.houseviewingapp.databinding.ActivityHouseRegistrationBinding
import com.capstone.houseviewingapp.home.HouseCardItem

class HouseRegistrationActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_QUICK_DIAGNOSIS_MODE = "extra_quick_diagnosis_mode"
    }

    private lateinit var binding: ActivityHouseRegistrationBinding
    private val vm: HouseRegistrationViewModel by viewModels()
    private var currentStep = 1 // TODO: 현재 단계에 따라 이 값을 업데이트
    private var isQuickDiagnosisMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHouseRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isQuickDiagnosisMode = intent.getBooleanExtra(EXTRA_QUICK_DIAGNOSIS_MODE, false)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottom = maxOf(systemBars.bottom, ime.bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottom)
            insets
        }

        if (savedInstanceState == null) {
            if(isQuickDiagnosisMode) {
                currentStep = 3
                supportFragmentManager.beginTransaction()
                    .replace(R.id.registerationFragmentContainer, HouseInfoStep3Fragment())
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.registerationFragmentContainer, HouseInfoStep1Fragment())
                    .commit()
            }
            updateStepUi()
        }

        binding.nextButton.setOnClickListener {
            //TODO: 다음 버튼 클릭 시 다음 단계로 이동하는 로직 구현
            if (currentStep == 1) {
                val step1 = supportFragmentManager
                    .findFragmentById(R.id.registerationFragmentContainer) as? HouseInfoStep1Fragment
                    ?: return@setOnClickListener

                val input =
                    step1.collectStep1Data() ?: return@setOnClickListener // null이면 다음 단계로 넘어가지 않음

                vm.updateStep1(
                    nickname = input.nickname,
                    originAddress = input.originAddress,
                    detailAddress = input.detailAddress
                )

                supportFragmentManager.beginTransaction()
                    .replace(R.id.registerationFragmentContainer, HouseInfoStep2Fragment())
                    .addToBackStack(null)
                    .commit()
                currentStep = 2
            } else if (currentStep == 2) {
                val step2 = supportFragmentManager
                    .findFragmentById(R.id.registerationFragmentContainer) as? HouseInfoStep2Fragment
                    ?: return@setOnClickListener

                val input = step2.collectStep2Data() ?: return@setOnClickListener
                vm.updateStep2(
                    contractType = input.contractType,
                    deposit = input.deposit,
                    monthlyAmount = input.monthlyAmount,
                    maintenanceFee = input.maintenanceFee,
                    moveDate = input.moveDate,
                    confirmDate = input.confirmDate
                )

                supportFragmentManager.beginTransaction()
                    .replace(R.id.registerationFragmentContainer, HouseInfoStep3Fragment())
                    .addToBackStack(null)
                    .commit()
                currentStep = 3
            } else if (currentStep == 3) {
                val step3 = supportFragmentManager
                    .findFragmentById(R.id.registerationFragmentContainer) as? HouseInfoStep3Fragment
                    ?: return@setOnClickListener
                if (!step3.hasSelectedFile()) {
                    return@setOnClickListener
                }
                if (isQuickDiagnosisMode) {
                    // TODO(백엔드): step3.getSelectedFileUriString() 업로드 -> 분석 시작 API 호출 -> jobId 수신
                    val intent = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        putExtra(MainActivity.EXTRA_SHOW_ANALYSIS_LOADING, true)
                        // TODO(백엔드): putExtra("analysis_job_id", jobId)
                    }
                    startActivity(intent)
                    finish()
                    return@setOnClickListener
                }

                // 기존 집 등록 플로우
                vm.updateStep3(step3.getSelectedFileUriString())
                val draft = vm.draft.value
                val address = listOf(draft.originAddress, draft.detailAddress)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                val cardItem = HouseCardItem(
                    houseId = null,
                    homeName = draft.nickname,
                    address = address,
                    ltv = null
                )
                HouseLocalStore.addHouse(this, cardItem)
                setResult(RESULT_OK)
                finish()
            }
            updateStepUi()
        }

        binding.backButton.setOnClickListener {
            if (isQuickDiagnosisMode) {
                finish()
                return@setOnClickListener
            }

            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
                currentStep = (currentStep - 1).coerceAtLeast(1)
                updateStepUi()
            } else {
                finish()
            }
        }

    }

    private fun updateStepUi() {
        if (isQuickDiagnosisMode) {
            binding.registrationProgressBar.visibility = View.GONE
            binding.stepTextView.text = "빠른 진단"
            binding.toolBarTextView.text = "PDF 문서 업로드"
            binding.nextButton.text = "분석 시작"
            setNextButtonEnabled(false) // Step3에서 파일 선택 시 Fragment가 true로 바꿔줌
            return
        }

        binding.registrationProgressBar.visibility = View.VISIBLE
        binding.registrationProgressBar.max = 100
        val progress = when (currentStep) {
            1 -> 34
            2 -> 67
            3 -> 100
            else -> 0
        }
        binding.registrationProgressBar.progress = progress
        binding.stepTextView.text = "$currentStep / 3 단계"

        if (currentStep == 1) {
            binding.toolBarTextView.text = "주택 기본 정보 입력"
            setNextButtonEnabled(true)
        }
        if (currentStep == 2) {
            binding.toolBarTextView.text = "계약 정보 입력"
            binding.nextButton.text = "다음"
            setNextButtonEnabled(false)
        }
        if (currentStep == 3) {
            binding.nextButton.text = "문서 검증"
            setNextButtonEnabled(false)
        }
    }

    fun setNextButtonEnabled(enabled: Boolean) {
        binding.nextButton.isEnabled = enabled // NOTE : 다음 버튼 활성화/비활성화 설정

        val colorRes = if (enabled) R.color.blue else R.color.icongray // 활성화 여부에 따른 색상 리소스 선택

        val colorInt =
            androidx.core.content.ContextCompat.getColor(this, colorRes) // 색상 리소스에서 실제 색상 값 가져오기

        binding.nextButton.backgroundTintList =
            android.content.res.ColorStateList.valueOf(colorInt) // 버튼 배경 색상 변경
    }
}