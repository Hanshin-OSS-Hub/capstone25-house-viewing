package com.house.houseviewing.domain.registrysnapshot.entity;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.pdfreport.entity.PdfReportEntity;
import com.house.houseviewing.domain.registrysnapshot.enums.RiskLevel;
import com.house.houseviewing.global.exception.AppException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrysnapshots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegistrySnapshotEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registrysnapshot_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private HouseEntity houseEntity;

    @OneToOne
    @JoinColumn(name = "pdfreport_id")
    private PdfReportEntity pdfReport;

    @Column(nullable = false)
    private String snapshotUrl; // 새 등기부 PDF 경로

    @Column(nullable = false)
    private String originalFileName; // 파일 이름

    @Column(columnDefinition = "json")
    private String rawData; // 등기부 json

    @Column(nullable = false)
    private Integer ltvScore; // ltv 값

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성 일

    @Column(nullable = false)
    private boolean isChanged; // 변동 여부

    public void addHouse(HouseEntity house){
        this.houseEntity = house;
    }

    @Builder
    public RegistrySnapshotEntity(Long id, HouseEntity houseEntity, PdfReportEntity pdfReport, String snapshotUrl, String originalFileName, String rawData, Integer ltvScore, RiskLevel riskLevel, LocalDateTime createdAt, boolean isChanged) {
        this.id = id;
        this.houseEntity = houseEntity;
        this.pdfReport = pdfReport;
        this.snapshotUrl = snapshotUrl;
        this.originalFileName = originalFileName;
        this.rawData = rawData;
        this.ltvScore = ltvScore;
        this.riskLevel = riskLevel;
        this.createdAt = createdAt;
        this.isChanged = isChanged;
    }
}
