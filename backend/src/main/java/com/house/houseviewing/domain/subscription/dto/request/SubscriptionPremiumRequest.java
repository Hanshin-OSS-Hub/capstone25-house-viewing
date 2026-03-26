package com.house.houseviewing.domain.subscription.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPremiumRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
}
