package com.capstone.houseviewingapp.house

import com.capstone.houseviewingapp.house.model.HouseRequest
import com.capstone.houseviewingapp.house.model.HouseResponse
import java.util.concurrent.atomic.AtomicLong

class MockHouseRepository : HouseRepository {
    private val idGenerator = AtomicLong(1L)
    private val houses = mutableListOf<HouseResponse>()

    override fun registerHouse(accessToken: String, request: HouseRequest): Result<HouseResponse> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        if (request.address.isBlank()) return Result.failure(IllegalArgumentException("ADDRESS_REQUIRED"))

        val house = HouseResponse(
            houseId = idGenerator.getAndIncrement(),
            address = request.address.trim()
        )
        houses.add(house)
        return Result.success(house)
    }

    override fun getHouses(accessToken: String): Result<List<HouseResponse>> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        return Result.success(houses.toList())
    }

    override fun getHouse(accessToken: String, houseId: Long): Result<HouseResponse> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        val house = houses.firstOrNull { it.houseId == houseId }
            ?: return Result.failure(NoSuchElementException("HOUSE_NOT_FOUND"))
        return Result.success(house)
    }

    override fun updateHouse(
        accessToken: String,
        houseId: Long,
        request: HouseRequest
    ): Result<HouseResponse> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        if (request.address.isBlank()) return Result.failure(IllegalArgumentException("ADDRESS_REQUIRED"))

        val index = houses.indexOfFirst { it.houseId == houseId }
        if (index == -1) return Result.failure(NoSuchElementException("HOUSE_NOT_FOUND"))

        val updated = houses[index].copy(address = request.address.trim())
        houses[index] = updated
        return Result.success(updated)
    }

    override fun deleteHouse(accessToken: String, houseId: Long): Result<Unit> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))

        val removed = houses.removeAll { it.houseId == houseId }
        if (!removed) return Result.failure(NoSuchElementException("HOUSE_NOT_FOUND"))
        return Result.success(Unit)
    }
}

