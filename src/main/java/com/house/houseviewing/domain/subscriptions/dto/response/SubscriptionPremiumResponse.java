package com.house.houseviewing.domain.subscriptions.dto.response;

import com.house.houseviewing.domain.subscriptions.enums.PlanType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubscriptionPremiumResponse {

    private Long userId;

    private Long subscriptionId;

    private PlanType planType;
}
