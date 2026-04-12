package com.capstone.houseviewingapp.analysis

import com.capstone.houseviewingapp.analysis.model.AnalysisPdfResponse
import com.capstone.houseviewingapp.analysis.model.AnalysisSummaryResponse
import com.capstone.houseviewingapp.analysis.model.PreContractDiagnosisRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicLong

class MockAnalysisRepository : AnalysisRepository {
    private val idGen = AtomicLong(1L)
    private val pdfIdGen = AtomicLong(1L)
    private val records = mutableListOf<AnalysisSummaryResponse>()
    private val isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    override fun preContractDiagnoses(
        accessToken: String,
        fileUri: String,
        request: PreContractDiagnosisRequest
    ): Result<AnalysisPdfResponse> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        if (fileUri.isBlank()) return Result.failure(IllegalArgumentException("FILE_REQUIRED"))
        if (request.nickname.isBlank()) return Result.failure(IllegalArgumentException("NICKNAME_REQUIRED"))
        if (request.address.isBlank()) return Result.failure(IllegalArgumentException("ADDRESS_REQUIRED"))

        addSummary("PRE_CONTRACT")
        return Result.success(
            AnalysisPdfResponse(
                pdfReportId = pdfIdGen.getAndIncrement(),
                filePath = "https://mock.local/analysis/pre/${System.currentTimeMillis()}.pdf"
            )
        )
    }

    override fun postContractDiagnoses(houseId: Long, fileUri: String): Result<AnalysisPdfResponse> {
        if (houseId <= 0L) return Result.failure(IllegalArgumentException("HOUSE_ID_INVALID"))
        if (fileUri.isBlank()) return Result.failure(IllegalArgumentException("FILE_REQUIRED"))

        addSummary("POST_CONTRACT")
        return Result.success(
            AnalysisPdfResponse(
                pdfReportId = pdfIdGen.getAndIncrement(),
                filePath = "https://mock.local/analysis/post/$houseId/${System.currentTimeMillis()}.pdf"
            )
        )
    }

    override fun changeDiagnoses(houseId: Long): Result<AnalysisPdfResponse> {
        if (houseId <= 0L) return Result.failure(IllegalArgumentException("HOUSE_ID_INVALID"))

        addSummary("CHANGE")
        return Result.success(
            AnalysisPdfResponse(
                pdfReportId = pdfIdGen.getAndIncrement(),
                filePath = "https://mock.local/analysis/diff/$houseId/${System.currentTimeMillis()}.pdf"
            )
        )
    }

    override fun getAnalyses(accessToken: String): Result<List<AnalysisSummaryResponse>> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        return Result.success(records.toList())
    }

    override fun getDiffAnalyses(accessToken: String): Result<List<AnalysisSummaryResponse>> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        return Result.success(records.filter { it.analysisType == "CHANGE" })
    }

    private fun addSummary(type: String) {
        records.add(
            0,
            AnalysisSummaryResponse(
                analysisId = idGen.getAndIncrement(),
                analysisType = type,
                createdAt = LocalDateTime.now().format(isoFormatter)
            )
        )
    }
}

