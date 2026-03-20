package com.house.houseviewing.domain.pdfreport.entity;

import com.house.houseviewing.domain.common.BaseTimeEntity;
import com.house.houseviewing.domain.common.DiagnosisType;
import com.house.houseviewing.domain.registryanalysis.entity.RegistryAnalysisEntity;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pdfreports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PdfReportEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pdfreport_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "registryanalysis_id", unique = true)
    private RegistryAnalysisEntity registryAnalysis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiagnosisType diagnosisType;

    @Column(nullable = false)
    private String pdfKey;

    @Column(nullable = false)
    private String pdfName;

    @Column(nullable = false)
    private String pdfPath;

    @Column(nullable = false)
    private Long pdfSizeBytes;

    @Builder
    public PdfReportEntity(String pdfKey, String pdfName, String pdfPath, Long pdfSizeBytes) {
        this.pdfKey = pdfKey;
        this.pdfName = pdfName;
        this.pdfPath = pdfPath;
        this.pdfSizeBytes = pdfSizeBytes;
    }

    public void addRegistryAnalysis(RegistryAnalysisEntity registryAnalysis){
        this.registryAnalysis = registryAnalysis;
        registryAnalysis.addPdfReport(this);
    }

    public void updateDiagnosisType(DiagnosisType diagnosisType){this.diagnosisType = diagnosisType;}
}
