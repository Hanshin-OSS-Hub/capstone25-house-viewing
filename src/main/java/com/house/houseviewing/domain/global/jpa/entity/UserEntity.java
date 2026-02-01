package com.house.houseviewing.domain.global.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_name")
    private String name;

    private String email;

    @Column(unique = true)
    private String loginId;

    private String password;

    public UserEntity(String name, String email, String loginId, String password) {
        this.name = name;
        this.email = email;
        this.loginId = loginId;
        this.password = password;
    }

    public void updatePassword(String password){
        this.password = password;
    }

}
