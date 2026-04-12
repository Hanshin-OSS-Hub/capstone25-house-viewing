package com.capstone.houseviewingapp.data.remote

import com.capstone.houseviewingapp.auth.model.ApiErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

private val gson = Gson()

suspend fun <T> Call<T>.executeApi(): Result<T> = withContext(Dispatchers.IO) {
    runCatching {
        val response = execute()
        if (response.isSuccessful) {
            response.body() ?: throw IllegalStateException("EMPTY_BODY")
        } else {
            throw response.toApiException()
        }
    }
}

suspend fun Call<Void>.executeApiVoid(): Result<Unit> = withContext(Dispatchers.IO) {
    runCatching {
        val response = execute()
        if (response.isSuccessful) {
            Unit
        } else {
            throw response.toApiException()
        }
    }
}

suspend fun Call<String>.executeApiString(): Result<String> = withContext(Dispatchers.IO) {
    runCatching {
        val response = execute()
        if (response.isSuccessful) {
            response.body().orEmpty()
        } else {
            throw response.toApiException()
        }
    }
}

private fun <T> Response<T>.toApiException(): RemoteApiException {
    val raw = errorBody()?.string()
    val parsed = raw?.let {
        runCatching { gson.fromJson(it, ApiErrorResponse::class.java) }.getOrNull()
    }
    return RemoteApiException(
        code = parsed?.code,
        message = parsed?.message ?: message()
    )
}
