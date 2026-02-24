package com.house.houseviewing.domain.registrysnapshot.entity;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrysnapshots")
@AllArgsConstructor @Builder
@NoArgsConstructor @Getter
public class RegistrySnapshotEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id")
    HouseEntity houseEntity;

    private String fileUrl;

    private String originalFileName;

    @Column(columnDefinition = "json")
    private String rawData;

    private LocalDateTime createdAt;

}
