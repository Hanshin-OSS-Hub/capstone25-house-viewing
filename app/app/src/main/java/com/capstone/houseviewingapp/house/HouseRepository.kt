package com.capstone.houseviewingapp.house

import com.capstone.houseviewingapp.house.model.HouseRequest
import com.capstone.houseviewingapp.house.model.HouseResponse

interface HouseRepository {
    fun registerHouse(accessToken: String, request: HouseRequest): Result<HouseResponse>
    fun getHouses(accessToken: String): Result<List<HouseResponse>>
    fun getHouse(accessToken: String, houseId: Long): Result<HouseResponse>
    fun updateHouse(accessToken: String, houseId: Long, request: HouseRequest): Result<HouseResponse>
    fun deleteHouse(accessToken: String, houseId: Long): Result<Unit>
}

