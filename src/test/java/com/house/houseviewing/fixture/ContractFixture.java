package com.house.houseviewing.fixture;

import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.contract.enums.ContractType;
import com.house.houseviewing.domain.house.entity.HouseEntity;

import java.time.LocalDate;

public class ContractFixture {

    public static ContractEntity.ContractEntityBuilder createDefault(HouseEntity house){
        return ContractEntity.builder()
                .houseEntity(house)
                .contractType(ContractType.JEONSE)
                .deposit(30000000L)
                .monthlyAmount(0L)
                .maintenanceFee(150000L)
                .moveDate(LocalDate.of(2026, 03, 01))
                .confirmDate(LocalDate.of(2026, 03, 07));
    }
}
