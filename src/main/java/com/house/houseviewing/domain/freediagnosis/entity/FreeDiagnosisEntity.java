package com.house.houseviewing.domain.freediagnosis.entity;

import com.house.houseviewing.domain.common.RiskLevel;
import com.house.houseviewing.domain.freediagnosis.enums.FreeStatus;
import com.house.houseviewing.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "free_diagnosis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FreeDiagnosisEntity{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id @Column(name = "freediagnosis_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    private String pdfUrl; // PDF 경로

    private String originFileName; // 파일 이름

    @Column(columnDefinition = "json")
    private String rawData; // json

    @Column(nullable = false)
    private String snapshotUrl; // 등기부 URL

    private Integer ltvScore;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FreeStatus freeStatus;

    @Builder
    public FreeDiagnosisEntity(String snapshotUrl, UserEntity userEntity, String pdfUrl, String originFileName, String rawData, Integer ltvScore, RiskLevel riskLevel, LocalDateTime createdAt, FreeStatus freeStatus) {
        this.userEntity = userEntity;
        this.pdfUrl = pdfUrl;
        this.originFileName = originFileName;
        this.rawData = rawData;
        this.ltvScore = ltvScore;
        this.riskLevel = riskLevel;
        this.createdAt = createdAt;
        this.freeStatus = freeStatus;
        this.snapshotUrl = snapshotUrl;
    }
}
