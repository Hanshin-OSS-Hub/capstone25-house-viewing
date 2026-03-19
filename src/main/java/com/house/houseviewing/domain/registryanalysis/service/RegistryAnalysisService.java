package com.house.houseviewing.domain.registryanalysis.service;

import com.house.houseviewing.domain.registryanalysis.entity.RegistryAnalysisEntity;
import com.house.houseviewing.domain.registryanalysis.repository.RegistryAnalysisRepository;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.global.file.snapshot.service.SnapshotAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegistryAnalysisService {

    private final RegistryAnalysisRepository registryAnalysisRepository;
    private final SnapshotAnalysisService snapshotAnalysisService;

    @Transactional
    public Long register(MultipartFile snapshot, RegistrySnapshotEntity registrySnapshot){
        RegistryAnalysisEntity analysis = snapshotAnalysisService.analyze(snapshot);
        RegistryAnalysisEntity save = registryAnalysisRepository.save(analysis);
        save.addRegistrySnapshot(registrySnapshot);
        return save.getId();
    }
}
