package com.example.houseviewing.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String name;

    private String loginId;

    private String password;

    public User(String email, String name, String loginId, String password) {
        this.email = email;
        this.name = name;
        this.loginId = loginId;
        this.password = password;
    }

    public void changePassword(String newPassword){
        this.password = newPassword;
    }
}
