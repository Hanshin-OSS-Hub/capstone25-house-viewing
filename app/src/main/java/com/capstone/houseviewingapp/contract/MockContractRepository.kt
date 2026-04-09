package com.capstone.houseviewingapp.contract

import com.capstone.houseviewingapp.contract.model.RegisterContractRequest
import com.capstone.houseviewingapp.contract.model.RegisterContractResponse
import java.util.concurrent.atomic.AtomicLong

class MockContractRepository : ContractRepository {
    private val contractIdGen = AtomicLong(1L)
    private val contracts = mutableMapOf<Long, RegisterContractResponse>()

    override fun registerContract(
        accessToken: String,
        request: RegisterContractRequest
    ): Result<RegisterContractResponse> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        if (request.houseId <= 0L) return Result.failure(NoSuchElementException("HOUSE_NOT_FOUND"))
        if (request.contractType.isBlank()) return Result.failure(IllegalArgumentException("CONTRACT_TYPE_REQUIRED"))
        if (request.contractDate.isBlank()) return Result.failure(IllegalArgumentException("CONTRACT_DATE_REQUIRED"))

        val response = RegisterContractResponse(
            houseId = request.houseId,
            contractId = contractIdGen.getAndIncrement()
        )
        contracts[response.contractId] = response
        return Result.success(response)
    }

    override fun deleteContract(accessToken: String, contractId: Long): Result<Unit> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        contracts.remove(contractId) ?: return Result.failure(NoSuchElementException("CONTRACT_NOT_FOUND"))
        return Result.success(Unit)
    }
}

