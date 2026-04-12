package com.capstone.houseviewingapp.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capstone.houseviewingapp.MainActivity
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.data.local.AuthTokenLocalStore
import com.capstone.houseviewingapp.data.local.BillingLocalStore
import com.capstone.houseviewingapp.data.local.HouseLocalStore
import com.capstone.houseviewingapp.data.local.QuickDiagnosisLocalStore
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
        refreshQuickDiagnosisBanner()
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

        parentFragmentManager.setFragmentResultListener(
            MainActivity.RESULT_BOTTOM_REFRESH,
            viewLifecycleOwner
        ) { _, result ->
            val targetId = result.getInt(MainActivity.RESULT_KEY_TARGET_ID, -1)
            if (targetId == R.id.nav_home) {
                loadAndShowCards()
                refreshQuickDiagnosisBanner()
            }
        }

        parentFragmentManager.setFragmentResultListener(
            EditHouseBottomSheetFragment.RESULT_KEY,
            viewLifecycleOwner
        ) { _, result ->
            val houseId = result.getLong(EditHouseBottomSheetFragment.RESULT_HOUSE_ID, -1L)
            val newName = result.getString(EditHouseBottomSheetFragment.RESULT_HOME_NAME).orEmpty()
            val newAddress = result.getString(EditHouseBottomSheetFragment.RESULT_ADDRESS).orEmpty()
            if (houseId <= 0L || newName.isBlank()) return@setFragmentResultListener

            val detail = HouseLocalStore.getHouseDetail(requireContext(), houseId) ?: return@setFragmentResultListener
            val normalizedAddress = newAddress.trim()
            val currentOrigin = detail.originAddress.trim()

            val (updatedOriginAddress, updatedDetailAddress) = when {
                normalizedAddress.isBlank() -> detail.originAddress to detail.detailAddress
                currentOrigin.isNotBlank() && normalizedAddress.startsWith(currentOrigin) -> {
                    currentOrigin to normalizedAddress.removePrefix(currentOrigin).trim()
                }
                else -> normalizedAddress to ""
            }

            val updated = detail.copy(
                homeName = newName,
                originAddress = updatedOriginAddress,
                detailAddress = updatedDetailAddress
            )
            HouseLocalStore.updateHouseDetailById(requireContext(), houseId, updated)
            loadAndShowCards()
        }
        parentFragmentManager.setFragmentResultListener(
            PaidQuickDiagnosisDialogFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->
            val shouldContinue = result.getBoolean(PaidQuickDiagnosisDialogFragment.KEY_CONTINUE, false)
            if (shouldContinue) {
                openQuickDiagnosisFlow()
            }
        }

        loadAndShowCards()

        //NOTE: 빈 화면에서는 버튼 클릭 시에만 집 등록
        binding.emptylayout.plusHouseButton.setOnClickListener {
            val intent = Intent(requireContext(), HouseRegistrationActivity::class.java)
            registerLauncher.launch(intent)
        }

        // TODO: startButton 경로만 quick 모드=true.
        // TODO: plusHouseButton/onAddClick은 일반 등록 플로우(1->2->3) 유지.
        binding.startButton.setOnClickListener {
            val loginId = AuthTokenLocalStore.getLoginId(requireContext()).orEmpty()
            val freeUsed = QuickDiagnosisLocalStore.isFreeUsed(requireContext(), loginId)
            if (freeUsed) {
                PaidQuickDiagnosisDialogFragment()
                    .show(parentFragmentManager, "PaidQuickDiagnosisDialog")
            } else {
                openQuickDiagnosisFlow()
            }
        }

    }

    // NOTE : 카드 로드. 화면 갱심 함수
    private fun loadAndShowCards() {
        val storedItems = HouseLocalStore.getHouses(requireContext())
        val items = storedItems
        val isPremium = BillingLocalStore.isPremium(requireContext())
        binding.viewpager.adapter = HouseCardAdapter(
            items,
            isPremium,
            onDelete = { _, index ->
                HouseLocalStore.removeHouse(requireContext(), index)
                loadAndShowCards()
            },
            onEdit = { item, _ ->
                val houseId = item.houseId ?: return@HouseCardAdapter
                val detail = HouseLocalStore.getHouseDetail(requireContext(), houseId) ?: return@HouseCardAdapter
                EditHouseBottomSheetFragment
                    .newInstance(
                        houseId = houseId,
                        homeName = detail.homeName,
                        address = detail.fullAddress()
                    )
                    .show(parentFragmentManager, EditHouseBottomSheetFragment.TAG)
            },
            onAddClick = {
                val intent = Intent(requireContext(), HouseRegistrationActivity::class.java)
                registerLauncher.launch(intent)
            },
            onCardClick = { item ->
                val houseId = item.houseId ?: return@HouseCardAdapter
                findNavController().navigate(
                    R.id.action_nav_home_to_nav_house_detail,
                    bundleOf("houseId" to houseId)
                )
            }
        )
        updateEmptyState(storedItems)
    }

    private fun refreshQuickDiagnosisBanner() {
        val loginId = AuthTokenLocalStore.getLoginId(requireContext()).orEmpty()
        val freeUsed = QuickDiagnosisLocalStore.isFreeUsed(requireContext(), loginId)
        if (freeUsed) {
            binding.bannerTextView.text = "부동산 안전 진단"
            binding.startButton.text = "진단하기 ->"
        } else {
            binding.bannerTextView.text = "1회 무료 부동산 안전 진단"
            binding.startButton.text = "무료 진단하기 ->"
        }
    }

    private fun openQuickDiagnosisFlow() {
        val intent = Intent(requireContext(), HouseRegistrationActivity::class.java).apply {
            putExtra(HouseRegistrationActivity.EXTRA_QUICK_DIAGNOSIS_MODE, true)
        }
        startActivity(intent)
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

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            loadAndShowCards()
            refreshQuickDiagnosisBanner()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}