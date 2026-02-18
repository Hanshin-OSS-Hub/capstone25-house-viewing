package com.house.houseviewing.domain.contract.model;

import com.house.houseviewing.domain.contract.enums.ContractType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ContractRegisterRQ {

    private Long houseId;

    @NotNull(message = "계약 유형을 입력해주세요.")
    private ContractType contractType;

    @NotNull(message = "보증금을 입력해주세요.")
    private Long deposit;

    @NotNull(message = "월세를 입력해주세요(전세는 해당 사항 없음).")
    @PositiveOrZero
    private Long monthlyAmount;

    @NotNull(message = "월 관리비를 입력해주세요.")
    private Long maintenanceFee;

    @NotNull(message = "전입날짜를 입력해주세요.")
    private LocalDateTime moveDate;

    @NotNull(message = "확정일자를 입력해주세요.")
    private LocalDateTime confirmDate;

    public ContractRegisterRQ(ContractType contractType, Long deposit, Long monthlyAmount, Long maintenanceFee, LocalDateTime moveDate, LocalDateTime confirmDate) {
        this.contractType = contractType;
        this.deposit = deposit;
        this.monthlyAmount = monthlyAmount;
        this.maintenanceFee = maintenanceFee;
        this.moveDate = moveDate;
        this.confirmDate = confirmDate;
    }
}
