package com.house.houseviewing.domain.registrysnapshot.dto.response;

import com.house.houseviewing.domain.common.RiskLevel;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
@Builder @AllArgsConstructor
public class SnapshotResultResponse {

    private String nickname;

    private String address;

    private String mainReason;

    private Integer ltvScore;

    private RiskLevel riskLevel;

    public static SnapshotResultResponse from(RegistrySnapshotEntity snapshot){
        HouseEntity house = snapshot.getHouseEntity();
        return SnapshotResultResponse.builder()
                .nickname(house.getNickname())
                .address(house.getAddress().getAddressName())
                .mainReason(snapshot.getMainReason())
                .ltvScore(snapshot.getLtvScore())
                .riskLevel(snapshot.getRiskLevel())
                .build();
    }
}
