package com.house.houseviewing.domain.pdfreport.entity;

import com.house.houseviewing.domain.common.BaseTimeEntity;
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

    @OneToOne(mappedBy = "pdfReport", cascade = CascadeType.ALL, optional = false)
    private RegistrySnapshotEntity registrySnapshot;

    @Column(nullable = false)
    private String pdfName;

    @Column(nullable = false)
    private String pdfPath;

    @Builder
    public PdfReportEntity(Long id, RegistrySnapshotEntity registrySnapshot, String pdfName, String pdfPath) {
        this.id = id;
        this.registrySnapshot = registrySnapshot;
        this.pdfName = pdfName;
        this.pdfPath = pdfPath;
    }
}
