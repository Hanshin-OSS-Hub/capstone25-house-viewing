package com.house.houseviewing.domain.user.entity;

import com.house.houseviewing.domain.common.BaseTimeEntity;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.subscription.enums.PlanType;
import com.house.houseviewing.domain.user.enums.MonitoringStatus;
import com.house.houseviewing.domain.subscription.entity.SubscriptionEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @NoArgsConstructor
@AllArgsConstructor @Builder
public class UserEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    public void updatePassword(String password){
        this.password = password;
    }

    @OneToMany(mappedBy = "userEntity")
    private List<HouseEntity> houses = new ArrayList<>();

    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private SubscriptionEntity subscription;

    public UserEntity(String password, String loginId, String email, String name) {
        this.password = password;
        this.loginId = loginId;
        this.email = email;
        this.name = name;
    }

    public void addHouse(HouseEntity house){
        houses.add(house);
        house.setUserEntity(this);
    }

    public boolean checkSubscription() {
        return this.getSubscription().getPlanType() == PlanType.PREMIUM;
    }
}
