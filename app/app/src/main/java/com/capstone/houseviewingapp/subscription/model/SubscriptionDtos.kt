package com.capstone.houseviewingapp.subscription.model

data class SubscribePremiumRequest(
    val userId: Long,
    val planType: String
)

data class SubscribePremiumResponse(
    val userId: Long,
    val subscriptionId: Long,
    val planType: String
)

