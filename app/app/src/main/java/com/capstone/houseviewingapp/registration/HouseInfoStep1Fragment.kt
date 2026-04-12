package com.capstone.houseviewingapp.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.FragmentHouseInfoStep1Binding

class HouseInfoStep1Fragment : Fragment(R.layout.fragment_house_info_step1) {
    private var _binding: FragmentHouseInfoStep1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHouseInfoStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    data class Step1Data(
        val nickname: String, // 집 닉네임
        val originAddress: String, // 도로명 주소
        val detailAddress: String // 상세 주소
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.houseNicknameEditText.addTextChangedListener { validateStep1Input() }
        binding.addressEditText.addTextChangedListener { validateStep1Input() }
        binding.addressDescEditText.addTextChangedListener { validateStep1Input() }

        validateStep1Input()
    }

    // NOTE : 버튼 활성화 비활성화 처리
    private fun validateStep1Input() {
        val nickname = binding.houseNicknameEditText.text?.toString()?.trim().orEmpty()
        val originAddress = binding.addressEditText.text?.toString()?.trim().orEmpty()
        val detailAddress = binding.addressDescEditText.text?.toString()?.trim().orEmpty()

        val ButtonEnable =
            nickname.isNotBlank() && originAddress.isNotBlank() && detailAddress.isNotBlank()
        (activity as? HouseRegistrationActivity)?.setNextButtonEnabled(ButtonEnable)

        // TODO: 주소 검색 API 붙이면 "검색 결과 선택 완료" 조건 추가
        // 예) canGoNext = nickname.isNotBlank() && originAddress.isNotBlank() && isAddressConfirmed
    }

    // NOTE : UI에서 사용자가 입력한 데이터를 모아서 Step1Data 클래스 인스턴스로 반환하는 함수
    fun collectStep1Data(): Step1Data? {
        val nickname = binding.houseNicknameEditText.text?.toString()?.trim().orEmpty()
        val originAddress = binding.addressEditText.text?.toString()?.trim().orEmpty()
        val detailAddress = binding.addressDescEditText.text?.toString()?.trim().orEmpty()

        if (nickname.isBlank()) return null
        if (originAddress.isBlank()) return null
        if (detailAddress.isBlank()) return null

        return Step1Data(
            nickname = nickname,
            originAddress = originAddress,
            detailAddress = detailAddress
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}