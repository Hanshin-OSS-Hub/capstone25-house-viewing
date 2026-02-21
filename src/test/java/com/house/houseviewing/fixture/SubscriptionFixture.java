package com.house.houseviewing.fixture;

import com.house.houseviewing.domain.subscription.entity.SubscriptionEntity;
import com.house.houseviewing.domain.subscription.enums.PlanType;
import com.house.houseviewing.domain.user.entity.UserEntity;

import java.time.LocalDateTime;

public class SubscriptionFixture {

    public static SubscriptionEntity.SubscriptionEntityBuilder createDefault(UserEntity user){
        return SubscriptionEntity.builder()
                .user(user)
                .planType(PlanType.FREE)
                .startedAt(LocalDateTime.now());
    }
}
