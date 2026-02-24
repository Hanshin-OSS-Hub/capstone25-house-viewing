package com.capstone.houseviewingapp.registration

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.FragmentHouseInfoStep2Binding
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar

class HouseInfoStep2Fragment : Fragment(R.layout.fragment_house_info_step2) {
    private var _binding: FragmentHouseInfoStep2Binding? = null
    private val binding get() = _binding!!
    // [B] setText() 재호출 무한루프 방지 플래그
    private var isFormattingMoveInDate = false
    private var isFormattingConfirmDate = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHouseInfoStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.noManageCheckBox.setOnCheckedChangeListener {
            _, isChecked ->
            binding.monthManageEditText.isEnabled = !isChecked
            if(isChecked) {
                binding.monthManageEditText.setText("0")
                binding.monthManageEditText.isEnabled = false
                binding.monthManageEditText.isFocusable = false
                binding.monthManageEditText.isFocusableInTouchMode = false
                binding.monthManageInputBox.alpha = 0.45f
                binding.monthManageWonText.alpha = 0.45f
            } else {
                binding.monthManageEditText.isEnabled = true
                binding.monthManageEditText.isFocusable = true
                binding.monthManageEditText.isFocusableInTouchMode = true
                binding.monthManageInputBox.alpha = 1f
                binding.monthManageWonText.alpha = 1f
            }
        }

        binding.fixedDateIcon.setOnClickListener {
            showMoveInDatePicker()
        }
        setupMoveInDateInputFormat()

        binding.confirmDateIcon.setOnClickListener {
            showConfirmDatePicker()
        }
        setupConfirmDateInputFormat()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // NOTE : 전입일 선택 다이얼로그 띄우는 함수
    private fun showMoveInDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("전입일 선택")
            .build()

        picker.addOnPositiveButtonClickListener { selectionMillis ->
            val formatted = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.KOREA)
                .format(java.util.Date(selectionMillis))
            binding.moveInDateEditText.setText(formatted)
        }
        picker.show(parentFragmentManager, "MOVE_IN_DATE_PICKER")
    }

    // NOTE : 전입일 EditText에 yyyy-MM-dd 형식 자동 포맷팅 적용하는 함수
    private fun setupMoveInDateInputFormat() {
        binding.moveInDateEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit


            override fun afterTextChanged(s: Editable?) {
                // [1] 포맷팅 중 재진입 방지
                if (isFormattingMoveInDate) return
                isFormattingMoveInDate = true

                // [2] 숫자만 추출하고 최대 8자리(yyyyMMdd)까지만 사용
                val digitsOnly = s.toString()
                    .filter { it.isDigit() }
                    .take(8)

                // [3] yyyy-MM-dd 형태로 문자열 재조합
                val formatted = buildString {
                    digitsOnly.forEachIndexed { index, c ->
                        append(c)
                        if (index == 3 || index == 5) append('-')
                    }
                }

                // [4] 현재 텍스트와 다를 때만 반영 (커서 포함)
                if (s.toString() != formatted) {
                    binding.moveInDateEditText.setText(formatted)
                    binding.moveInDateEditText.setSelection(formatted.length)
                }

                // [5] 포맷팅 종료
                isFormattingMoveInDate = false
            }
        })
    }

    // NOTE : 확정일 선택 다이얼로그 띄우는 함수
    private fun showConfirmDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("확정일자 선택")
            .build()

        picker.addOnPositiveButtonClickListener { selectionMillis ->
            val formatted = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.KOREA)
                .format(java.util.Date(selectionMillis))
            binding.confirmDateEditText.setText(formatted)
        }
        picker.show(parentFragmentManager, "CONFIRM_DATE_PICKER")
    }

    // NOTE : 확정일자 EditText에 yyyy-MM-dd 형식 자동 포맷팅 적용하는 함수
    private fun setupConfirmDateInputFormat() {
        binding.confirmDateEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit


            override fun afterTextChanged(s: Editable?) {
                // [1] 포맷팅 중 재진입 방지
                if (isFormattingConfirmDate) return
                isFormattingConfirmDate = true

                // [2] 숫자만 추출하고 최대 8자리(yyyyMMdd)까지만 사용
                val digitsOnly = s.toString()
                    .filter { it.isDigit() }
                    .take(8)

                // [3] yyyy-MM-dd 형태로 문자열 재조합
                val formatted = buildString {
                    digitsOnly.forEachIndexed { index, c ->
                        append(c)
                        if (index == 3 || index == 5) append('-')
                    }
                }

                // [4] 현재 텍스트와 다를 때만 반영 (커서 포함)
                if (s.toString() != formatted) {
                    binding.confirmDateEditText.setText(formatted)
                    binding.confirmDateEditText.setSelection(formatted.length)
                }

                // [5] 포맷팅 종료
                isFormattingConfirmDate = false
            }
        })
    }
}