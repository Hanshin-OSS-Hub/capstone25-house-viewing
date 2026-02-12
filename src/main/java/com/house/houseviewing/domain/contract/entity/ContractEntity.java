package com.house.houseviewing.domain.contract.entity;

import com.house.houseviewing.domain.common.BaseTimeEntity;
import com.house.houseviewing.domain.contract.enums.ContractType;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "contracts")
@Getter
public class ContractEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "house_id")
    private HouseEntity houseEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractType contractType;

    @Column(nullable = false)
    private Long deposit;

    private Long monthlyAmount;

    @Column(nullable = false)
    private Long maintenanceFee;

    private LocalDateTime moveDate;

    private LocalDateTime confirmDate;



}
