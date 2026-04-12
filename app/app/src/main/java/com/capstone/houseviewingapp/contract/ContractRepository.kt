package com.capstone.houseviewingapp.contract

import com.capstone.houseviewingapp.contract.model.RegisterContractRequest
import com.capstone.houseviewingapp.contract.model.RegisterContractResponse

interface ContractRepository {
    fun registerContract(
        accessToken: String,
        request: RegisterContractRequest
    ): Result<RegisterContractResponse>

    fun deleteContract(
        accessToken: String,
        contractId: Long
    ): Result<Unit>
}

