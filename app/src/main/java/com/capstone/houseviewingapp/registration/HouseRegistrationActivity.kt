package com.capstone.houseviewingapp.registration

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.ActivityHouseRegistrationBinding

class HouseRegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHouseRegistrationBinding
    private var currentStep = 1 // TODO: 현재 단계에 따라 이 값을 업데이트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHouseRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.registerationFragmentContainer, HouseInfoStep1Fragment())
                .commit()
            updateStepUi()
        }

        binding.nextButton.setOnClickListener {
            //TODO: 다음 버튼 클릭 시 다음 단계로 이동하는 로직 구현
            if(currentStep == 1) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.registerationFragmentContainer, HouseInfoStep2Fragment())
                    .addToBackStack(null)
                    .commit()
                    currentStep = 2
            } else if (currentStep == 2) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.registerationFragmentContainer, HouseInfoStep3Fragment())
                    .addToBackStack(null)
                    .commit()
                currentStep = 3
            } else if (currentStep == 3) {

            }

            updateStepUi()

        }

        binding.backButton.setOnClickListener {
            if(supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
                currentStep = (currentStep - 1).coerceAtLeast(1) // 현재 단계가 1보다 작아지지 않도록 보장
                updateStepUi()
            }
            else {
                finish() // 백스택이 비어있으면 액티비티 종료
            }
        }

    }

    private fun updateStepUi() {
        binding.registrationProgressBar.max = 100
        val progress = when(currentStep) {
            1 -> 34
            2 -> 67
            3 -> 100
            else -> 0
        }

        binding.registrationProgressBar.progress = progress
        binding.stepTextView.text = "$currentStep / 3 단계"
        if(currentStep == 1) {
            binding.toolBarTextView.text = "주택 기본 정보 입력"
            setNextButtonEnabled(true)
        }
        if (currentStep == 2) {
            binding.toolBarTextView.text = "계약 정보 입력"
            binding.nextButton.text = "다음"
            setNextButtonEnabled(true)
        }
        if (currentStep == 3) {
            binding.nextButton.text = "문서 검증"
            setNextButtonEnabled(false) // TODO: 3단계에서 다음 버튼은 문서 검증이 완료된 후에만 활성화되도록 구현
        }

    }

    fun setNextButtonEnabled(enabled: Boolean) {
        binding.nextButton.isEnabled = enabled // NOTE : 다음 버튼 활성화/비활성화 설정

        val colorRes = if (enabled) R.color.blue else R.color.icongray // 활성화 여부에 따른 색상 리소스 선택

        val colorInt = androidx.core.content.ContextCompat.getColor(this, colorRes) // 색상 리소스에서 실제 색상 값 가져오기

        binding.nextButton.backgroundTintList = android.content.res.ColorStateList.valueOf(colorInt) // 버튼 배경 색상 변경
    }
}