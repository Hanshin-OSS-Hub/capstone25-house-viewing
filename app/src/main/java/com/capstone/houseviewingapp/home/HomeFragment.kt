package com.capstone.houseviewingapp.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.data.local.HouseLocalStore
import com.capstone.houseviewingapp.databinding.FragmentHomeBinding
import com.capstone.houseviewingapp.registration.HouseRegistrationActivity

class HomeFragment : Fragment (R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val registerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        loadAndShowCards()
    }

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

        loadAndShowCards()

        //NOTE: 빈 화면에서는 버튼 클릭 시에만 집 등록
        binding.emptylayout.plusHouseButton.setOnClickListener {
            val intent = Intent(requireContext(), HouseRegistrationActivity::class.java)
            registerLauncher.launch(intent)
        }

        // TODO: startButton 경로만 quick 모드=true.
        // TODO: plusHouseButton/onAddClick은 일반 등록 플로우(1->2->3) 유지.
        binding.startButton.setOnClickListener {
            val intent = Intent(requireContext(), HouseRegistrationActivity::class.java).apply{
                putExtra(HouseRegistrationActivity.EXTRA_QUICK_DIAGNOSIS_MODE, true)
            }
            startActivity(intent)
        }

    }

    // NOTE : 카드 로드. 화면 갱심 함수
    private fun loadAndShowCards() {
        val items = HouseLocalStore.getHouses(requireContext())
        binding.viewpager.adapter = HouseCardAdapter(
            items,
            // TODO : 수정 기능 구현 시 onEdit 람다 추가 예정
//            onEdit = { item, index ->
//                val intent = Intent(requireContext(), HouseRegistrationActivity::class.java)
//                intent.putExtra(HouseRegistrationActivity.EXTRA_EDIT_INDEX, index)
//                registerLauncher.launch(intent)
//            },
           onDelete = { _, index ->
                HouseLocalStore.removeHouse(requireContext(), index)
                loadAndShowCards()
            },
            onAddClick = {
                val intent = Intent(requireContext(), HouseRegistrationActivity::class.java)
                registerLauncher.launch(intent)
            }
        )
        updateEmptyState(items)
    }

    // NOTE : 카드 목록이 비어있는지 여부에 따라 빈 화면과 카드 뷰의 표시 상태를 업데이트하는 함수
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