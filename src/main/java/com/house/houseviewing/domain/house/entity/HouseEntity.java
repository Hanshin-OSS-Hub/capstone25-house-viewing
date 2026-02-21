package com.house.houseviewing.domain.house.entity;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.common.BaseTimeEntity;
import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.enums.MonitoringStatus;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "houses")
@NoArgsConstructor @Getter
public class HouseEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "house_id")
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @OneToMany(mappedBy = "houseEntity")
    private List<ContractEntity> contracts = new ArrayList<>();

    @Column(nullable = false)
    private String nickname;

    @Embedded
    private Address address;

    private Integer ltvScore;

    @Enumerated(EnumType.STRING)
    private MonitoringStatus monitoringStatus;

    public void addContract(ContractEntity contract){
        checkContract(contract);
        contracts.add(contract);
        contract.setHouseEntity(this);
    }

    public HouseEntity(String nickname, Address address, Integer ltvScore) {
        this.nickname = nickname;
        this.address = address;
        this.ltvScore = ltvScore;
    }

    public void checkContract(ContractEntity contract) {
        if(!this.contracts.isEmpty())
            throw new AppException(ExceptionCode.ALREADY_REGISTERED_CONTRACT);
    }
}
