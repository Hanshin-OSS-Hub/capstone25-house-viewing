package com.capstone.houseviewingapp.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.FragmentHomeBinding
import com.capstone.houseviewingapp.registration.HouseRegistrationActivity

class HomeFragment : Fragment (R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val items = emptyList<HouseCardItem>()

//         NOTE: ViewPager2에 카드 아이템들 연결하기 (나중에 API에서 받아온 데이터로 교체 예정)
//        val items = listOf<HouseCardItem>(
//            HouseCardItem("서초 그랑자이 104동", "서울특별시 서초구 서초동 1234-56", 50),
//            HouseCardItem("마포 래미안 102동", "서울특별시 마포구 공덕동 11-22", 72),
//            HouseCardItem("강남 아이파크 203동", "서울특별시 강남구 역삼동 33-44", 66)
//        )

        binding.viewpager.adapter = HouseCardAdapter(items)

        updateEmptyState(items)

        //NOTE: 빈 화면에서 집 정보를 직접 입력하고 추가하는 방식
        binding.emptylayout.plusHouseButton.setOnClickListener {
            Toast.makeText(requireContext(), "살려줘....", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), HouseRegistrationActivity::class.java)
            startActivity(intent)
        }


    }

    private fun updateEmptyState(items: List<HouseCardItem>) {
        if (items.isEmpty()) {
            binding.viewpager.visibility = View.GONE
            binding.emptylayout.root.visibility = View.VISIBLE // NOTE: include 부분 -> root 추가 이유 : include 전체를 가리키기 위해 (다른 xml 파일이여서 root 필요)
        } else {
            binding.viewpager.visibility = View.VISIBLE
            binding.emptylayout.root.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}