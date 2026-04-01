package com.house.houseviewing.domain.house.dto.response;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.subscription.dto.response.SubscriptionMeResponse;
import com.house.houseviewing.domain.user.dto.response.UserMeResponse;
import com.house.houseviewing.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor @Builder
public class HouseRegisterResponse {

    private Long houseId;

    public static HouseRegisterResponse from(Long houseId){
        return HouseRegisterResponse.builder()
                .houseId(houseId)
                .build();
    }
}

