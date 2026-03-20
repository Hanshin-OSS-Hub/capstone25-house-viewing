package com.house.houseviewing.domain.registryanalysis.entity;

import com.house.houseviewing.domain.common.BaseTimeEntity;
import com.house.houseviewing.domain.common.DiagnosisType;
import com.house.houseviewing.domain.common.RiskLevel;
import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.pdfreport.entity.PdfReportEntity;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "houses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegistryAnalysisEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registryanalysis_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrysnapshot_id")
    private RegistrySnapshotEntity snapshot;

    @OneToOne
    @JoinColumn(name = "contract_id", unique = true)
    private ContractEntity contract;

    @OneToOne(mappedBy = "registryAnalysis")
    private PdfReportEntity pdfReport;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiagnosisType diagnosisType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Column(columnDefinition = "json", nullable = false)
    private String rawData;

    @Column(nullable = false)
    private String mainReason;

    @Column(nullable = false)
    private Integer ltvScore;

    @Builder
    public RegistryAnalysisEntity(DiagnosisType diagnosisType, RiskLevel riskLevel, String rawData, String mainReason, Integer ltvScore) {
        this.diagnosisType = diagnosisType;
        this.riskLevel = riskLevel;
        this.rawData = rawData;
        this.mainReason = mainReason;
        this.ltvScore = ltvScore;
    }

    public void addContract(ContractEntity contract){
        this.contract = contract;
        contract.addRegistryAnalysis(this);
    }

    public void addRegistrySnapshot(RegistrySnapshotEntity registrySnapshot){
        this.snapshot = registrySnapshot;
        registrySnapshot.addAnalysis(this);
    }

    public void addPdfReport(PdfReportEntity pdfReport) {this.pdfReport = pdfReport;}
}
