package com.house.houseviewing.domain.analysis.postanalysis.entity;

import com.house.houseviewing.domain.analysis.postanalysis.enums.AnalysisType;
import com.house.houseviewing.domain.common.BaseTimeEntity;
import com.house.houseviewing.domain.common.RiskLevel;
import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.report.postreport.entity.PostReportEntity;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_analyses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostAnalysisEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snapshot_id")
    private RegistrySnapshotEntity snapshot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", unique = true)
    private ContractEntity contract;

    @OneToOne(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private PostReportEntity pdfReport;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    private AnalysisType analysisType;

    @Column(columnDefinition = "json", nullable = false)
    private String rawData;

    @Column(nullable = false)
    private String mainReason;

    @Column(nullable = false)
    private Integer ltvScore;

    @Builder
    public PostAnalysisEntity(RiskLevel riskLevel, AnalysisType analysisType, String rawData, String mainReason, Integer ltvScore) {
        this.riskLevel = riskLevel;
        this.analysisType = analysisType;
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

    public void addPdfReport(PostReportEntity pdfReport) {this.pdfReport = pdfReport;}
}
