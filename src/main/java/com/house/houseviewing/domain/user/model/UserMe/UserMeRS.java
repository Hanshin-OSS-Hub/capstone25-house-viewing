package com.house.houseviewing.domain.user.model.UserMe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor @Builder
@Getter
public class UserMeRS {

    private Long userId;

    private String loginId;
}
