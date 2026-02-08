package com.house.houseviewing.domain.house.entity;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.common.BaseTimeEntity;
import com.house.houseviewing.domain.house.enums.MonitoringStatus;
import com.house.houseviewing.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor @Getter
public class HouseEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "house_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    private String nickname;

    @Embedded
    private Address address;

    private Integer ltvScore;

    @Enumerated(EnumType.STRING)
    MonitoringStatus monitoringStatus;
}
