package com.house.houseviewing.domain.user.model.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter @Builder
public class UserLoginRS {

    private String token;

    private String loginId;

    private Long userId;
}
