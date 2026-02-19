package com.house.houseviewing.domain.user.entity;

import com.house.houseviewing.domain.common.BaseTimeEntity;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.subscription.entity.SubscriptionEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @NoArgsConstructor
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

    public void addHouse(HouseEntity house){
        houses.add(house);
        house.setUserEntity(this);
    }

    @OneToMany(mappedBy = "user")
    private List<SubscriptionEntity> subscriptions = new ArrayList<>();

    public UserEntity(String name, String email, String loginId, String password) {
        this.name = name;
        this.email = email;
        this.loginId = loginId;
        this.password = password;
    }



}
