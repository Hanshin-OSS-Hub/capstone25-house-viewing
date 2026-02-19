package com.house.houseviewing.domain.subscription.model;

import com.house.houseviewing.domain.subscription.enums.PlanType;
import lombok.Builder;

@Builder
public class SubscriptionUpdateRS {

    private Long userId;

    private Long subscriptionId;

    private PlanType planType;
}
