package com.house.houseviewing.domain.registrysnapshot.service;

import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshot.repository.RegistrySnapshotRepository;
import com.house.houseviewing.infrastructure.python.model.PythonAnalysisRS;
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
    public RegistrySnapshotEntity register(PythonAnalysisRS response, String originFileName){
        RegistrySnapshotEntity snapshotEntity = RegistrySnapshotEntity.builder()
                .originalFileName(originFileName)
                .rawData(response.getRawData())
                .ltvScore(response.getLtvScore())
                .createdAt(LocalDateTime.now())
                .build();
        RegistrySnapshotEntity save = registrySnapshotRepository.save(snapshotEntity);
        return save;
    }

}
