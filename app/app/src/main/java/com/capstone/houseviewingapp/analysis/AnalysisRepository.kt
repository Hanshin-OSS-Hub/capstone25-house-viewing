package com.capstone.houseviewingapp.analysis

import com.capstone.houseviewingapp.analysis.model.AnalysisResponse
import com.capstone.houseviewingapp.analysis.model.PdfDownloadResponse
import com.capstone.houseviewingapp.analysis.model.PreContractDiagnosisRequest

interface AnalysisRepository {
    fun preContractDiagnoses(
        accessToken: String,
        fileUri: String,
        request: PreContractDiagnosisRequest
    ): Result<PdfDownloadResponse>

    fun postContractDiagnoses(
        accessToken: String,
        houseId: Long,
        fileUri: String
    ): Result<PdfDownloadResponse>

    fun changeDiagnoses(
        accessToken: String,
        houseId: Long
    ): Result<PdfDownloadResponse>

    fun getAnalyses(accessToken: String): Result<List<AnalysisResponse>>
    fun getDiffAnalyses(accessToken: String): Result<List<AnalysisResponse>>
}
