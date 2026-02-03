package com.house.houseviewing.domain.house.entity;

import com.house.houseviewing.domain.house.enums.MonitoringStatus;
import com.house.houseviewing.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor @Getter
public class HouseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "house_id")
    private Long id;

    @Column(name = "user_id")
    private UserEntity id;

    private String nickname;

    @Enumerated(EnumType.STRING)
    MonitoringStatus monitoringStatus;

    private LocalDateTime createdAt;
}
