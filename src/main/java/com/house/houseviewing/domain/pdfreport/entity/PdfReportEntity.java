package com.house.houseviewing.domain.pdfreport.entity;

import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pdfreports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PdfReportEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pdfreport_id")
    private Long id;

    @OneToOne(mappedBy = "pdfReport", cascade = CascadeType.ALL, optional = false)
    private RegistrySnapshotEntity registrySnapshot;

    @Column(nullable = false)
    private String reportUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public PdfReportEntity(Long id, RegistrySnapshotEntity registrySnapshot, String reportUrl, LocalDateTime createdAt) {
        this.id = id;
        this.registrySnapshot = registrySnapshot;
        this.reportUrl = reportUrl;
        this.createdAt = createdAt;
    }

    public void addRegistrySnapshot(RegistrySnapshotEntity registrySnapshot){
        this.registrySnapshot = registrySnapshot;
    }
}
