package com.capstone.houseviewingapp.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.data.local.HouseLocalStore
import com.capstone.houseviewingapp.data.local.model.HouseDetailItem
import com.capstone.houseviewingapp.databinding.FragmentHouseDetailBinding
import java.text.NumberFormat
import java.util.Locale

class HouseDetailFragment : Fragment(R.layout.fragment_house_detail) {

    private var _binding: FragmentHouseDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHouseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack(R.id.nav_home, false)
        }
        binding.moreButton.setOnClickListener { }

        val houseId = arguments?.getLong("houseId") ?: -1L
        if (houseId <= 0L) {
            findNavController().popBackStack(R.id.nav_home, false)
            return
        }

        val detail = HouseLocalStore.getHouseDetail(requireContext(), houseId)
        if (detail == null) {
            findNavController().popBackStack(R.id.nav_home, false)
            return
        }

        bindDetail(detail)
    }

    private fun bindDetail(detail: HouseDetailItem) {
        binding.homeNameTextView.text = detail.homeName
        binding.addressTextView.text = detail.fullAddress()

        binding.depositTextView.text = formatMoneyCompact(detail.deposit)
        binding.moveDateTextView.text = detail.moveDate.ifBlank { "-" }
        binding.confirmDateTextView.text = detail.confirmDate.ifBlank { "-" }

        val isJeonse = detail.contractType.equals("JEONSE", ignoreCase = true)
        if (isJeonse || detail.monthlyAmount <= 0L) {
            binding.monthlyRow.visibility = View.GONE
            binding.dividerAfterMonthly.visibility = View.GONE
        } else {
            binding.monthlyRow.visibility = View.VISIBLE
            binding.dividerAfterMonthly.visibility = View.VISIBLE
            binding.monthlyTextView.text = formatMoneyCompact(detail.monthlyAmount)
        }

        if (detail.maintenanceFee <= 0L) {
            binding.maintenanceRow.visibility = View.GONE
            binding.dividerAfterMaintenance.visibility = View.GONE
        } else {
            binding.maintenanceRow.visibility = View.VISIBLE
            binding.dividerAfterMaintenance.visibility = View.VISIBLE
            binding.maintenanceTextView.text = formatMoneyCompact(detail.maintenanceFee)
        }

        bindLtv(detail.ltv)

        val hasDocument = !detail.documentUri.isNullOrBlank()
        binding.viewOriginalPdfButton.isEnabled = hasDocument
        binding.viewOriginalPdfButton.alpha = if (hasDocument) 1f else 0.45f
        binding.documentHintTextView.text = if (hasDocument) {
            "원본 등기부등본 PDF를 앱 안에서 확인할 수 있어요."
        } else {
            "아직 연결된 원본 PDF가 없어요."
        }

        binding.viewOriginalPdfButton.setOnClickListener {
            val documentUri = detail.documentUri
            if (documentUri.isNullOrBlank()) {
                Toast.makeText(requireContext(), "등록된 원본 PDF가 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(
                Intent(requireContext(), PdfViewerActivity::class.java).apply {
                    putExtra(PdfViewerActivity.EXTRA_URI, documentUri)
                    putExtra(PdfViewerActivity.EXTRA_TITLE, "${detail.homeName} 원본 등기부등본")
                }
            )
        }

        binding.reportIssueButton.setOnClickListener {
            val title = Uri.encode("[집좀보자] 문서 이상 신고")
            val body = Uri.encode(
                "집 이름: ${detail.homeName}\n주소: ${detail.fullAddress()}\n문서 URI: ${detail.documentUri.orEmpty()}\n\n이상 내용을 작성해 주세요."
            )
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@houseviewing.app?subject=$title&body=$body")
            }
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "메일 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindLtv(ltv: Int?) {
        binding.ltvProgress.max = 100
        val trackColor = ContextCompat.getColor(requireContext(), R.color.textviewgray)
        binding.ltvProgress.trackColor = trackColor
        // TODO: UI 체크가 끝나면 fallbackLtv 제거하고 ltv가 null일 때 "--" 처리로 되돌리기.
        val fallbackLtv = 52
        val safeValue = (ltv ?: fallbackLtv).coerceIn(0, 100)
        binding.ltvValueTextView.text = "$safeValue%"
        binding.ltvProgress.progress = safeValue

        val colorRes = when {
            safeValue <= 60 -> R.color.blue
            safeValue <= 70 -> R.color.amber
            else -> R.color.red
        }
        val color = ContextCompat.getColor(requireContext(), colorRes)
        binding.ltvProgress.setIndicatorColor(color)
        binding.ltvValueTextView.setTextColor(color)
    }

    private fun formatMoneyCompact(value: Long): String {
        if (value <= 0L) return "0원"
        val eok = value / 100_000_000L
        val man = (value % 100_000_000L) / 10_000L
        return when {
            eok > 0 && man > 0 -> "${eok}억 ${man}만 원"
            eok > 0 -> "${eok}억 원"
            value >= 10_000L -> "${value / 10_000L}만 원"
            else -> "${NumberFormat.getNumberInstance(Locale.KOREA).format(value)}원"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}