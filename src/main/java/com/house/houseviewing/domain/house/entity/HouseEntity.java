package com.house.houseviewing.domain.house.entity;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.common.BaseTimeEntity;
import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.house.enums.MonitoringStatus;
import com.house.houseviewing.domain.user.entity.UserEntity;
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

    @OneToMany(mappedBy = "contract_id")
    private List<ContractEntity> contracts = new ArrayList<>();

    @Column(nullable = false)
    private String nickname;

    @Embedded
    @Column(nullable = false)
    private Address address;

    private Integer ltvScore;

    @Enumerated(EnumType.STRING)
    private MonitoringStatus monitoringStatus;

    public void addContract(ContractEntity contract){
        contracts.add(contract);
        contract.setHouseEntity(this);
    }

    public HouseEntity(String nickname, Address address, Integer ltvScore, MonitoringStatus monitoringStatus) {
        this.nickname = nickname;
        this.address = address;
        this.ltvScore = ltvScore;
        this.monitoringStatus = monitoringStatus;
    }
}
