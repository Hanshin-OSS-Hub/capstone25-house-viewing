package com.capstone.houseviewingapp.analysis

import com.capstone.houseviewingapp.analysis.model.AnalysisPdfResponse
import com.capstone.houseviewingapp.analysis.model.AnalysisSummaryResponse
import com.capstone.houseviewingapp.analysis.model.PreContractDiagnosisRequest

interface AnalysisRepository {
    fun preContractDiagnoses(
        accessToken: String,
        fileUri: String,
        request: PreContractDiagnosisRequest
    ): Result<AnalysisPdfResponse>

    fun postContractDiagnoses(
        houseId: Long,
        fileUri: String
    ): Result<AnalysisPdfResponse>

    fun changeDiagnoses(
        houseId: Long
    ): Result<AnalysisPdfResponse>

    fun getAnalyses(accessToken: String): Result<List<AnalysisSummaryResponse>>
    fun getDiffAnalyses(accessToken: String): Result<List<AnalysisSummaryResponse>>
}

