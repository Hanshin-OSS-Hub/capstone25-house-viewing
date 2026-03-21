package com.house.houseviewing.domain.subscriptions.dto.response;

import com.house.houseviewing.domain.subscriptions.entity.SubscriptionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor @Builder
public class SubscriptionMeResponse {

    private String planType;

    private LocalDateTime expiredAt;

    public static SubscriptionMeResponse from(SubscriptionEntity subscription){
        return SubscriptionMeResponse.builder()
                .planType(subscription.getPlanType().name())
                .expiredAt(subscription.getEndedAt())
                .build();
    }
}
