package com.house.houseviewing.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;

    private String email;

    private String loginId;

    private String password;

    public User(String name, String email, String loginId, String password) {
        this.name = name;
        this.email = email;
        this.loginId = loginId;
        this.password = password;
    }

}
