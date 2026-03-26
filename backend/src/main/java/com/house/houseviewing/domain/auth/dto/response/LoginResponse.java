package com.house.houseviewing.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter @Builder
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    public static LoginResponse from(String accessToken, String refreshToken){
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
