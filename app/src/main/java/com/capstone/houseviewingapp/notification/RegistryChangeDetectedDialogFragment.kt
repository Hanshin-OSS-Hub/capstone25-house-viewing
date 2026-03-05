package com.capstone.houseviewingapp.notification

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.capstone.houseviewingapp.MainActivity
import com.capstone.houseviewingapp.R
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
            dismiss()
            (requireActivity() as? MainActivity)?.let { activity ->
                (activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment)
                    ?.navController
                    ?.navigate(R.id.nav_analysis_loading)
            }
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