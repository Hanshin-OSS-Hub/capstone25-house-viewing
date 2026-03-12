package com.house.houseviewing.domain.user.dto.response;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor @Builder
@Getter
public class UserMeResponse {

    private String name;
    private String email;
    private String loginId;
    private SubscriptionInfo subscriptionInfo;
    private List<HouseEntity> houses;

}
