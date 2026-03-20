package com.house.houseviewing.domain.registryanalysis.service;

import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.contract.repository.ContractRepository;
import com.house.houseviewing.domain.registryanalysis.entity.RegistryAnalysisEntity;
import com.house.houseviewing.domain.registryanalysis.repository.RegistryAnalysisRepository;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.file.snapshot.service.SnapshotAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegistryAnalysisService {

    private final ContractRepository contractRepository;
    private final RegistryAnalysisRepository registryAnalysisRepository;
    private final SnapshotAnalysisService snapshotAnalysisService;

    @Transactional
    public RegistryAnalysisEntity register(MultipartFile snapshot, RegistrySnapshotEntity registrySnapshot){
        Long houseId = registrySnapshot.getHouse().getId();
        ContractEntity contract = contractRepository.findTopByHouseIdOrderByCreatedAtDesc(houseId)
                .orElseThrow(() -> new AppException(ExceptionCode.CONTRACT_NOT_FOUND));
        RegistryAnalysisEntity analysis = snapshotAnalysisService.analyze(snapshot);
        analysis.addRegistrySnapshot(registrySnapshot);
        analysis.addContract(contract);
        return registryAnalysisRepository.save(analysis);
    }
}
