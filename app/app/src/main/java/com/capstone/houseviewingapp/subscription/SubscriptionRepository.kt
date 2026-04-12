package com.capstone.houseviewingapp.subscription

import com.capstone.houseviewingapp.subscription.model.SubscribePremiumRequest
import com.capstone.houseviewingapp.subscription.model.SubscribePremiumResponse

interface SubscriptionRepository {
    fun subscribePremium(
        accessToken: String,
        request: SubscribePremiumRequest
    ): Result<SubscribePremiumResponse>
}

