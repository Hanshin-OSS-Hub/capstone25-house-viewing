package com.capstone.houseviewingapp.analysis

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.capstone.houseviewingapp.MainActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.data.local.AnalysisLocalStore
import com.capstone.houseviewingapp.databinding.FragmentAnalysisBinding
import com.capstone.houseviewingapp.home.PdfViewerActivity

class AnalysisFragment : Fragment(R.layout.fragment_analysis) {

    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!

    private lateinit var recordAdapter: AnalysisRecordAdapter
    private var allRecords: List<AnalysisRecordItem> = emptyList()

    private enum class RecordTab { MY, AUTO }
    private var selectedTab: RecordTab = RecordTab.MY

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnalysisBinding.bind(view)

        parentFragmentManager.setFragmentResultListener(
            MainActivity.RESULT_BOTTOM_REFRESH,
            viewLifecycleOwner
        ) { _, result ->
            val targetId = result.getInt(MainActivity.RESULT_KEY_TARGET_ID, -1)
            if (targetId == R.id.nav_analysis) {
                allRecords = AnalysisLocalStore.getRecords(requireContext())
                applyFilters()
            }
        }

        setupRecycler()
        setupTabs()
        setupFilterEvents()

        allRecords = AnalysisLocalStore.getRecords(requireContext())
        applyFilters()
    }
    private fun setupRecycler() {
        recordAdapter = AnalysisRecordAdapter(
            onLongClickDelete = { item ->
                AlertDialog.Builder(requireContext())
                    .setTitle("기록 삭제")
                    .setMessage("이 진단 기록을 삭제할까요?")
                    .setPositiveButton("삭제") { _, _ ->
                        AnalysisLocalStore.removeRecord(requireContext(), item)
                        allRecords = AnalysisLocalStore.getRecords(requireContext())
                        applyFilters()
                    }
                    .setNegativeButton("취소", null)
                    .show()
            },
            onDetailClick = { item ->
                val uriRaw = item.sourcePdfUri
                    ?.takeIf { it.isNotBlank() }
                    ?: ReportPdfDummyFactory.createOrGet(requireContext(), item).toString()
                startActivity(
                    Intent(requireContext(), PdfViewerActivity::class.java).apply {
                        putExtra(PdfViewerActivity.EXTRA_URI, uriRaw)
                        putExtra(PdfViewerActivity.EXTRA_TITLE, "${item.title} 상세 대응 리포트")
                        putExtra(PdfViewerActivity.EXTRA_SHOW_REPORT_BUTTON, true)
                    }
                )
            }
        )
        binding.recordRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recordRecyclerView.adapter = recordAdapter
    }
    private fun setupTabs() = with(binding) {
        tabMyRecord.setOnClickListener { selectTab(RecordTab.MY, animate = true) }
        tabAutoRecord.setOnClickListener { selectTab(RecordTab.AUTO, animate = true) }

        tabLayout.post { selectTab(selectedTab, animate = false) }
    }

    private fun selectTab(tab: RecordTab, animate: Boolean) = with(binding) {
        selectedTab = tab

        val blue = ContextCompat.getColor(requireContext(), R.color.blue)
        val gray = ContextCompat.getColor(requireContext(), R.color.textgray)
        val bold = ResourcesCompat.getFont(requireContext(), R.font.pretendard_bold)
        val medium = ResourcesCompat.getFont(requireContext(), R.font.pretendard_medium)

        val mySelected = tab == RecordTab.MY
        tabMyRecord.setTextColor(if (mySelected) blue else gray)
        tabAutoRecord.setTextColor(if (mySelected) gray else blue)
        tabMyRecord.typeface = if (mySelected) bold else medium
        tabAutoRecord.typeface = if (mySelected) medium else bold

        val target = if (mySelected) tabMyRecord else tabAutoRecord

        tabIndicator.layoutParams = tabIndicator.layoutParams.apply {
            width = target.width
        }
        tabIndicator.requestLayout()

        val targetX = target.x
        if (animate) {
            tabIndicator.animate()
                .x(targetX)
                .setDuration(180)
                .start()
        } else {
            tabIndicator.x = targetX
        }

        applyFilters()
    }

    private fun setupFilterEvents() {
        binding.filterChipGroup.setOnCheckedStateChangeListener { _, _ ->
            applyFilters()
        }
    }

    private fun applyFilters() {
        val bySource = when (selectedTab) {
            RecordTab.MY -> allRecords.filter { it.source == RecordSource.MANUAL }
            RecordTab.AUTO -> allRecords.filter { it.source == RecordSource.AUTO }
        }

        val filtered = bySource.filter { item ->
            when (binding.filterChipGroup.checkedChipId) {
                R.id.chipRed -> item.level == RiskLevel.RED
                R.id.chipAmber -> item.level == RiskLevel.AMBER
                R.id.chipBlue -> item.level == RiskLevel.BLUE
                else -> true // chipAll
            }
        }

        renderRecords(filtered)
    }

    private fun renderRecords(records: List<AnalysisRecordItem>) {
        val isEmpty = records.isEmpty()
        binding.emptyLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recordRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        recordAdapter.submit(records)
    }

    override fun onResume() {
        super.onResume()
        allRecords = com.capstone.houseviewingapp.data.local.AnalysisLocalStore.getRecords(requireContext())
        applyFilters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}