package com.house.houseviewing.domain.house.entity;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.common.BaseTimeEntity;
import com.house.houseviewing.domain.house.enums.MonitoringStatus;
import com.house.houseviewing.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String nickname;

    @Embedded
    private Address address;

    private Integer ltvScore;

    @Enumerated(EnumType.STRING)
    MonitoringStatus monitoringStatus;

    public HouseEntity(String nickname, Address address) {
        this.nickname = nickname;
        this.address = address;
    }
}
