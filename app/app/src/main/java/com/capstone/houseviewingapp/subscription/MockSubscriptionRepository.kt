package com.capstone.houseviewingapp.subscription

class MockSubscriptionRepository : SubscriptionRepository {
    override fun subscribePremium(accessToken: String): Result<Unit> {
        if (accessToken.isBlank()) return Result.failure(IllegalStateException("UNAUTHORIZED"))
        return Result.success(Unit)
    }
}
