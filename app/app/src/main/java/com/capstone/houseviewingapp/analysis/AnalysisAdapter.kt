package com.capstone.houseviewingapp.analysis

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.ItemAnalysisRecordBinding

class AnalysisRecordAdapter(
    private val onLongClickDelete: (item: AnalysisRecordItem) -> Unit,
    private val onDetailClick: (item: AnalysisRecordItem) -> Unit
) : RecyclerView.Adapter<AnalysisRecordAdapter.RecordVH>() {
    private data class UiSpec(
        val badgeText: String,
        val badgeBgRes: Int,
        val textColorRes: Int,
        val iconRes: Int
    )

    private val items = mutableListOf<AnalysisRecordItem>()

    fun submit(newItems: List<AnalysisRecordItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordVH {
        val binding = ItemAnalysisRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecordVH(binding)
    }

    override fun onBindViewHolder(holder: RecordVH, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnLongClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onLongClickDelete(items[pos])
            }
            true
        }
    }

    override fun getItemCount(): Int = items.size

    inner class RecordVH(private val binding: ItemAnalysisRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AnalysisRecordItem) {
            binding.titleTextView.text = item.title
            binding.addressTextView.text = item.address
            binding.riskTextView.text = summarizeRisk(item.riskSummary)

            val normalizedScore = item.ltv?.toInt()?.coerceIn(0, 100)
            val displayLevel = normalizedScore?.let { score ->
                when {
                    score <= 60 -> RiskLevel.BLUE
                    score <= 70 -> RiskLevel.AMBER
                    else -> RiskLevel.RED
                }
            } ?: item.level

            val (badgeText, badgeBgRes, textColorRes, iconRes) = when (displayLevel) {
                RiskLevel.RED -> UiSpec(
                    "고위험",
                    R.drawable.bg_analysis_badge_red,
                    R.color.risk_red_text,
                    R.drawable.baseline_warning_24
                )

                RiskLevel.AMBER -> UiSpec(
                    "주의",
                    R.drawable.bg_analysis_badge_amber,
                    R.color.risk_amber_text,
                    R.drawable.baseline_warning_24
                )

                RiskLevel.BLUE -> UiSpec(
                    "안전",
                    R.drawable.bg_analysis_badge_blue,
                    R.color.risk_blue_text,
                    R.drawable.round_check_circle_24
                )
            }

            val c = ContextCompat.getColor(binding.root.context, textColorRes)
            binding.riskBadgeTextView.setTextColor(c)

            binding.riskBadgeTextView.text = badgeText
            binding.riskBadgeTextView.setBackgroundResource(badgeBgRes)

            binding.titleTextView.setTextColor(c)
            binding.scoreTextView.setTextColor(c)

            binding.riskIconView.setImageResource(iconRes)
            binding.riskIconView.setColorFilter(c)

            binding.scoreTextView.text = normalizedScore?.let { "${it}점" } ?: "--점"
            binding.detailButton.text = "상세 리포트 확인"
            binding.detailButton.setOnClickListener { onDetailClick(item) }
        }

        private fun summarizeRisk(raw: String): String {
            val text = raw.trim().replace(Regex("\\s+"), " ")
            if (text.isBlank()) return "-"
            val splitToken = listOf(". ", " / ", "; ", "다만 ").firstOrNull { text.contains(it) }
            val primary = if (splitToken != null) text.substringBefore(splitToken).trim() else text
            return if (primary.length > 40) primary.take(40).trimEnd() + "..." else primary
        }
    }
}