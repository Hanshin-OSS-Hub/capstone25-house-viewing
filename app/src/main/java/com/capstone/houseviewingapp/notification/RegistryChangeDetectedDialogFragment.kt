package com.capstone.houseviewingapp.notification

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.capstone.houseviewingapp.databinding.DialogRegistryChangeDetectedBinding

class RegistryChangeDetectedDialogFragment: DialogFragment() {
    private var _binding: DialogRegistryChangeDetectedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRegistryChangeDetectedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        // 창 너비를 화면의 90%로 제한 (높이는 wrap_content 유지)
        val width = (resources.displayMetrics.widthPixels * 0.9f).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.analysisButton.setOnClickListener {
            // TODO : 나중에 분석화면 구현시 분석 화면으로 이동하도록 수정
            dismiss()
        }
        binding.buttonLater.setOnClickListener {
            dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}