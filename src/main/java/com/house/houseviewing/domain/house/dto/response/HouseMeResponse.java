package com.house.houseviewing.domain.house.dto.response;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor @Builder
public class HouseMeResponse {

    private Long houseId;

    private String nickname;

    private String address;

    public static HouseMeResponse from(HouseEntity house){
        return HouseMeResponse.builder()
                .houseId(house.getId())
                .nickname(house.getNickname())
                .address(house.getAddress().getAddressName())
                .build();
    }
}
