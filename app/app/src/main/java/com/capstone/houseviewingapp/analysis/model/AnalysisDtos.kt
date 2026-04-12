package com.capstone.houseviewingapp.analysis.model

/** 백엔드 RiskLevel — UI용 [com.capstone.houseviewingapp.analysis.AnalysisRecordItem.RiskLevel] 과 구분 */
enum class ApiRiskLevel {
    SAFE,
    WARNING,
    DANGER
}

data class PreContractDiagnosisRequest(
    val nickname: String,
    val address: String
)

/** PdfDownloadResponse */
data class PdfDownloadResponse(
    val pdfReportId: Long,
    val filePath: String
)

/** AnalysisResponse (목록 조회 /analyses) */
data class AnalysisResponse(
    val nickname: String,
    val address: String,
    val mainReason: String?,
    val riskLevel: ApiRiskLevel?,
    val ltvScore: Int?
)
