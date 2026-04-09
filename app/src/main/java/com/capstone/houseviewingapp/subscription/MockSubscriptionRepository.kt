package com.capstone.houseviewingapp.subscription

import com.capstone.houseviewingapp.subscription.model.SubscribePremiumRequest
import com.capstone.houseviewingapp.subscription.model.SubscribePremiumResponse
import java.util.concurrent.atomic.AtomicLong

class MockSubscriptionRepository : SubscriptionRepository {
    private val subscriptionIdGen = AtomicLong(1L)

    override fun subscribePremium(
        accessToken: String,
        request: SubscribePremiumRequest
    ): Result<SubscribePremiumResponse> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        if (request.userId <= 0L) return Result.failure(NoSuchElementException("USER_NOT_FOUND"))
        if (request.planType.isBlank()) return Result.failure(NoSuchElementException("SUBSCRIPTION_NOT_FOUND"))

        return Result.success(
            SubscribePremiumResponse(
                userId = request.userId,
                subscriptionId = subscriptionIdGen.getAndIncrement(),
                planType = request.planType
            )
        )
    }
}

