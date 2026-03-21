package com.house.houseviewing.domain.house.dto.response;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.postanalysis.entity.RegistryAnalysisEntity;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
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

        if (!house.getSnapshots().isEmpty()){
            RegistrySnapshotEntity snapshot = house.getSnapshots()
                    .get(house.getSnapshots().size() - 1);
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
