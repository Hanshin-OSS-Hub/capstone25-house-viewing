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

    private String fileUrl; // 새 등기부 PDF 경

    private String reportFileUrl; // 변동 분석 PDF 경로

    private String originalFileName; // 파일 이름

    @Column(columnDefinition = "json")
    private String rawData; // 등기부 json

    private Integer ltvScore; // ltv 값

    private LocalDateTime createdAt; // 생성 일

    private boolean isChanged; // 변동 여부

}
