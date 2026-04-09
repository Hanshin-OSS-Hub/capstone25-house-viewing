package com.capstone.houseviewingapp.analysis.model

data class PreContractDiagnosisRequest(
    val nickname: String,
    val address: String
)

data class AnalysisPdfResponse(
    val pdfReportId: Long,
    val filePath: String
)

data class AnalysisSummaryResponse(
    val analysisId: Long,
    val analysisType: String,
    val createdAt: String
)

