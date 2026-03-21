package com.house.houseviewing.domain.houses.dto.response;

import com.house.houseviewing.domain.contracts.entity.ContractEntity;
import com.house.houseviewing.domain.postcontractanalyses.entity.RegistryAnalysisEntity;
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

    public static HouseMeResponse from(ContractEntity contract, RegistryAnalysisEntity analysis){

        return HouseMeResponse.builder()
                .nickname(contract.getHouse().getNickname())
                .address(contract.getHouse().getAddress().getAddressName())
                .deposit(contract.getDeposit())
                .monthlyAmount(contract.getMonthlyAmount())
                .maintenanceFee(contract.getMaintenanceFee())
                .moveDate(contract.getMoveDate())
                .confirmDate(contract.getConfirmDate())
                .ltvScore(analysis.getLtvScore())
                .build();
    }
}
