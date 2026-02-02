package com.capstone.houseviewingapp.login

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.houseviewingapp.databinding.DialogFindIdSuccessBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.core.graphics.drawable.toDrawable

class FindIDResultFragment(private val id: String ) : BottomSheetDialogFragment() { // NOTE : id가 찾은 아이디 (저런건 val -> 수정될 일 없으니깐)
    private var _binding : DialogFindIdSuccessBinding? = null
    private val binding get() =  _binding!!

    override fun onCreateView( // NOTE : 화면 생성
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogFindIdSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // NOTE : 화면 꾸미기, 기능 넣기
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        binding.foundIDTextView.text = "아이디 : $id"

        binding.loginButton.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP // NOTE : 뒤로가기 꼬임 방지
            startActivity(intent)
            dismiss() // NOTE : 팝업창 종료
        }
        binding.findPWButton.setOnClickListener {
            val intent = Intent(requireContext(), FindPasswordActivity::class.java)
            startActivity(intent)
            dismiss()
            requireActivity().finish() // NOTE: 아이디 찾기 화면 종료
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}