package com.capstone.houseviewingapp.contract.model

data class RegisterContractRequest(
    val houseId: Long,
    val contractType: String,
    val contractDate: String
)

data class RegisterContractResponse(
    val houseId: Long,
    val contractId: Long
)

