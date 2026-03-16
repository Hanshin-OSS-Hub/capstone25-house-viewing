package com.house.houseviewing.domain.registrysnapshot.entity;

import com.house.houseviewing.domain.common.BaseTimeEntity;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.pdfreport.entity.PdfReportEntity;
import com.house.houseviewing.domain.common.RiskLevel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "registrysnapshots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegistrySnapshotEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registrysnapshot_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private HouseEntity house;

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

    private String mainReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private boolean isChanged; // 변동 여부

    @Builder
    public RegistrySnapshotEntity(HouseEntity house, PdfReportEntity pdfReport, String snapshotUrl, String originalFileName, String rawData, Integer ltvScore, String mainReason, RiskLevel riskLevel, boolean isChanged) {
        this.house = house;
        this.pdfReport = pdfReport;
        this.snapshotUrl = snapshotUrl;
        this.originalFileName = originalFileName;
        this.rawData = rawData;
        this.ltvScore = ltvScore;
        this.mainReason = mainReason;
        this.riskLevel = riskLevel;
        this.isChanged = isChanged;
    }

    public void addHouse(HouseEntity houseEntity) {
        this.house = houseEntity;
    }
}
