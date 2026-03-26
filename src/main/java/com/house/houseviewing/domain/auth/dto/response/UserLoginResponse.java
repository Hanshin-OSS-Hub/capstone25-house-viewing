package com.house.houseviewing.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter @Builder
public class UserLoginResponse {

    private String accessToken;

    private String refreshToken;

    public static UserLoginResponse from(String accessToken, String refreshToken){
        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
