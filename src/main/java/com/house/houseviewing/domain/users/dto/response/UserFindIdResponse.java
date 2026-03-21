package com.house.houseviewing.domain.users.dto.response;

import com.house.houseviewing.domain.users.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor @Builder
public class UserFindIdResponse {

    private String loginId;

    public static UserFindIdResponse from(UserEntity user){
        return UserFindIdResponse.builder()
                .loginId(user.getLoginId())
                .build();
    }
}
