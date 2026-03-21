package com.house.houseviewing.fixture;

import com.house.houseviewing.domain.subscriptions.entity.SubscriptionEntity;
import com.house.houseviewing.domain.subscriptions.enums.PlanType;
import com.house.houseviewing.domain.subscriptions.dto.request.SubscriptionPremiumRequest;
import com.house.houseviewing.domain.users.entity.UserEntity;

public class SubscriptionFixture {

    public static SubscriptionEntity.SubscriptionEntityBuilder createDefault(UserEntity user){
        return SubscriptionEntity.builder()
                .user(user)
                .planType(PlanType.FREE);
    }

    public static SubscriptionPremiumRequest.SubscriptionPremiumRQBuilder createPremium(SubscriptionEntity subscription){
        return SubscriptionPremiumRequest.builder()
                .userId(subscription.getUser().getId());
    }
}
