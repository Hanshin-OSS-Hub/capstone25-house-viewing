package com.capstone.houseviewingapp.analysis

enum class RiskLevel { RED, AMBER, BLUE }

enum class RecordSource {MANUAL, AUTO}

data class AnalysisRecordItem(
    val title: String,
    val address: String,
    val riskSummary: String, // 주요 원인
    val level: RiskLevel,
    val source: RecordSource, // 수동 입력인지 자동 분석인지
    val ltv: Double? = null
)
