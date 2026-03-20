package com.house.houseviewing.domain.registrysnapshot.entity;

import com.house.houseviewing.domain.common.BaseTimeEntity;
import com.house.houseviewing.domain.common.DiagnosisType;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.registryanalysis.entity.RegistryAnalysisEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registrysnapshots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegistrySnapshotEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registrysnapshot_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = true)
    private HouseEntity house;

    @OneToMany(mappedBy = "snapshot")
    private List<RegistryAnalysisEntity> analysis = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiagnosisType diagnosisType;

    @Column(nullable = true)
    private String preNickname;

    @Column(nullable = false)
    private String snapshotUrl; // 새 등기부 PDF 경로

    @Column(nullable = false)
    private String snapshotName; // 파일 이름

    @Column(nullable = false)
    private Long snapshotSizeBytes;

    @Builder
    public RegistrySnapshotEntity(DiagnosisType diagnosisType, String preNickname, String snapshotUrl, String snapshotName, Long snapshotSizeBytes) {
        this.diagnosisType = diagnosisType;
        this.preNickname = preNickname;
        this.snapshotUrl = snapshotUrl;
        this.snapshotName = snapshotName;
        this.snapshotSizeBytes = snapshotSizeBytes;
    }

    public void updatePreNickname(String preNickname) { this.preNickname = preNickname; }
    public void updateDiagnosisType(DiagnosisType diagnosisType) {this.diagnosisType = diagnosisType;}
    public void addHouse(HouseEntity houseEntity) {
        this.house = houseEntity;
    }
    public void addAnalysis(RegistryAnalysisEntity analysis){ this.analysis.add(analysis); }
}
