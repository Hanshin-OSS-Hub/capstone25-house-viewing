package com.capstone.houseviewingapp.analysis

import com.capstone.houseviewingapp.analysis.model.AnalysisResponse
import com.capstone.houseviewingapp.analysis.model.PdfDownloadResponse
import com.capstone.houseviewingapp.analysis.model.PreContractDiagnosisRequest
import com.capstone.houseviewingapp.analysis.model.ApiRiskLevel
import java.util.concurrent.atomic.AtomicLong

class MockAnalysisRepository : AnalysisRepository {
    private val pdfIdGen = AtomicLong(1L)
    private val records = mutableListOf<AnalysisResponse>()

    override fun preContractDiagnoses(
        accessToken: String,
        fileUri: String,
        request: PreContractDiagnosisRequest
    ): Result<PdfDownloadResponse> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        if (fileUri.isBlank()) return Result.failure(IllegalArgumentException("FILE_REQUIRED"))
        if (request.nickname.isBlank()) return Result.failure(IllegalArgumentException("NICKNAME_REQUIRED"))
        if (request.address.isBlank()) return Result.failure(IllegalArgumentException("ADDRESS_REQUIRED"))

        addRecord(request.nickname, request.address, ApiRiskLevel.WARNING)
        return Result.success(
            PdfDownloadResponse(
                pdfReportId = pdfIdGen.getAndIncrement(),
                filePath = "https://mock.local/analysis/pre/${System.currentTimeMillis()}.pdf"
            )
        )
    }

    override fun postContractDiagnoses(
        accessToken: String,
        houseId: Long,
        fileUri: String
    ): Result<PdfDownloadResponse> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        if (houseId <= 0L) return Result.failure(IllegalArgumentException("HOUSE_ID_INVALID"))
        if (fileUri.isBlank()) return Result.failure(IllegalArgumentException("FILE_REQUIRED"))

        addRecord("post-$houseId", "mock address", ApiRiskLevel.SAFE)
        return Result.success(
            PdfDownloadResponse(
                pdfReportId = pdfIdGen.getAndIncrement(),
                filePath = "https://mock.local/analysis/post/$houseId/${System.currentTimeMillis()}.pdf"
            )
        )
    }

    override fun changeDiagnoses(accessToken: String, houseId: Long): Result<PdfDownloadResponse> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        if (houseId <= 0L) return Result.failure(IllegalArgumentException("HOUSE_ID_INVALID"))

        addRecord("change-$houseId", "mock address", ApiRiskLevel.DANGER)
        return Result.success(
            PdfDownloadResponse(
                pdfReportId = pdfIdGen.getAndIncrement(),
                filePath = "https://mock.local/analysis/diff/$houseId/${System.currentTimeMillis()}.pdf"
            )
        )
    }

    override fun getAnalyses(accessToken: String): Result<List<AnalysisResponse>> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        return Result.success(records.toList())
    }

    override fun getDiffAnalyses(accessToken: String): Result<List<AnalysisResponse>> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        return Result.success(records.filter { it.riskLevel == ApiRiskLevel.DANGER })
    }

    private fun addRecord(nickname: String, address: String, risk: ApiRiskLevel) {
        records.add(
            0,
            AnalysisResponse(
                nickname = nickname,
                address = address,
                mainReason = "mock",
                riskLevel = risk,
                ltvScore = 50
            )
        )
    }
}
