package com.capstone.houseviewingapp.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}