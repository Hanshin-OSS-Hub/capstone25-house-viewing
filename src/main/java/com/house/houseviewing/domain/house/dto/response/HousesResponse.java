package com.house.houseviewing.domain.house.dto.response;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.user.enums.MonitoringStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor @Builder
public class HousesResponse {

    private Long houseId;

    private String nickname;

    private String address;

    private Integer ltvScore;

    private MonitoringStatus monitoringStatus;

    public static HousesResponse from(HouseEntity house){

         Integer ltvScore = null;

        if (!house.getRegistrySnapshots().isEmpty()){
            ltvScore = house.getRegistrySnapshots()
                    .get(house.getRegistrySnapshots().size() - 1)
                    .getLtvScore();
        }

        return HousesResponse.builder()
                .houseId(house.getId())
                .nickname(house.getNickname())
                .address(house.getAddress().getAddressName())
                .ltvScore(ltvScore)
                .monitoringStatus(house.getMonitoringStatus())
                .build();
    }
}
