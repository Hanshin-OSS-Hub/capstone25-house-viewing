package com.capstone.houseviewingapp.house.model

data class HouseRequest(
    val address: String
)

data class HouseResponse(
    val houseId: Long,
    val address: String
)

