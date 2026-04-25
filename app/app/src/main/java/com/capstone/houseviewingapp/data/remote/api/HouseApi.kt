package com.capstone.houseviewingapp.data.remote.api

import com.capstone.houseviewingapp.registration.remote.HouseRegisterRequest
import com.capstone.houseviewingapp.registration.remote.HouseRegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface HouseApi {
    @POST("houses/register")
    fun register(
        @Header("Authorization") authorization: String,
        @Body body: HouseRegisterRequest
    ): Call<HouseRegisterResponse>
}
