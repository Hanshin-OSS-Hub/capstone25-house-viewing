package com.house.houseviewing.domain.registrysnapshot.service;

import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshot.repository.RegistrySnapshotRepository;
import com.house.houseviewing.infrastructure.python.model.analysis.PythonAnalysisRS;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class RegistrySnapshotService {

    private final RegistrySnapshotRepository registrySnapshotRepository;

    @Transactional
    public Long register(PythonAnalysisRS response, String originFileName, String path){
        RegistrySnapshotEntity snapshotEntity = RegistrySnapshotEntity.builder()
                .originalFileName(originFileName)
                .fileUrl(path)
                .rawData(response.getRawData())
                .ltvScore(response.getLtvScore())
                .createdAt(LocalDateTime.now())
                .isChanged(false)
                .build();
        RegistrySnapshotEntity savedEntity =registrySnapshotRepository.save(snapshotEntity);
        return savedEntity.getId();
    }
}
