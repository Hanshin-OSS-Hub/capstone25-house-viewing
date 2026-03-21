package com.house.houseviewing.domain.users.dto.response;

import com.house.houseviewing.domain.subscriptions.dto.response.SubscriptionMeResponse;
import com.house.houseviewing.domain.users.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor @Builder
@Getter
public class UserMeResponse {

    private String name;
    private String email;
    private SubscriptionMeResponse subscription;

    public static UserMeResponse from(UserEntity user){
        return UserMeResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .subscription(SubscriptionMeResponse.from(user.getSubscription()))
                .build();
    }
}
