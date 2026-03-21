package com.house.houseviewing.domain.houses.dto.response;

import com.house.houseviewing.domain.houses.entity.HouseEntity;
import com.house.houseviewing.domain.postcontractanalyses.entity.RegistryAnalysisEntity;
import com.house.houseviewing.domain.registrysnapshots.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.users.enums.MonitoringStatus;
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
            RegistrySnapshotEntity snapshot = house.getRegistrySnapshots()
                    .get(house.getRegistrySnapshots().size() - 1);
            if(!snapshot.getAnalysis().isEmpty()){
                RegistryAnalysisEntity analysis = snapshot.getAnalysis().get(snapshot.getAnalysis().size() - 1);

                ltvScore = analysis.getLtvScore();
            }
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
