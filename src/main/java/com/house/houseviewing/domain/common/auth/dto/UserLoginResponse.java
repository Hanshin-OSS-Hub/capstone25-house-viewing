package com.house.houseviewing.domain.common.auth.dto;

import com.house.houseviewing.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter @Builder
public class UserLoginResponse {

    private String accessToken;

    public static UserLoginResponse from(String token){
        return UserLoginResponse.builder()
                .accessToken(token)
                .build();
    }
}
