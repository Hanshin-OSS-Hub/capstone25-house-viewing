package com.house.houseviewing.domain.house.dto.response;

import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.registryanalysis.entity.RegistryAnalysisEntity;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor @Getter
@AllArgsConstructor @Builder
public class HouseMeResponse {

    private String nickname;

    private String address;

    private Long deposit;

    private Long monthlyAmount;

    private Long maintenanceFee;

    private LocalDate moveDate;

    private LocalDate confirmDate;

    private Integer ltvScore;

    public static HouseMeResponse from(HouseEntity house, ContractEntity contract, RegistryAnalysisEntity analysis){

        return HouseMeResponse.builder()
                .nickname(house.getNickname())
                .address(house.getAddress().getAddressName())
                .deposit(contract.getDeposit())
                .monthlyAmount(contract.getMonthlyAmount())
                .maintenanceFee(contract.getMaintenanceFee())
                .moveDate(contract.getMoveDate())
                .confirmDate(contract.getConfirmDate())
                .ltvScore(analysis.getLtvScore())
                .build();
    }
}
