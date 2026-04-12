package com.capstone.houseviewingapp.data.remote

class RemoteApiException(
    val code: String?,
    override val message: String
) : Exception(message)
