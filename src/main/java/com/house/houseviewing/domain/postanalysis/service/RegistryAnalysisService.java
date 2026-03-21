package com.house.houseviewing.domain.postanalysis.service;

import com.house.houseviewing.domain.common.DiagnosisType;
import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.contract.repository.ContractRepository;
import com.house.houseviewing.domain.postanalysis.dto.response.AnalysisResponse;
import com.house.houseviewing.domain.postanalysis.entity.RegistryAnalysisEntity;
import com.house.houseviewing.domain.postanalysis.repository.RegistryAnalysisRepository;
import com.house.houseviewing.domain.registrysnapshot.dto.request.PreContractDiagnosisRequest;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.file.snapshot.service.SnapshotAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegistryAnalysisService {

    private final ContractRepository contractRepository;
    private final RegistryAnalysisRepository registryAnalysisRepository;
    private final SnapshotAnalysisService snapshotAnalysisService;

    @Transactional
    public RegistryAnalysisEntity postRegister(MultipartFile snapshot, RegistrySnapshotEntity registrySnapshot){
        Long houseId = registrySnapshot.getHouse().getId();
        ContractEntity contract = contractRepository.findTopByHouseIdOrderByCreatedAtDesc(houseId)
                .orElseThrow(() -> new AppException(ExceptionCode.CONTRACT_NOT_FOUND));
        RegistryAnalysisEntity analysis = snapshotAnalysisService.analyze(snapshot);
        analysis.addRegistrySnapshot(registrySnapshot);
        analysis.addContract(contract);
        analysis.updateDiagnosisType(DiagnosisType.POSTCONTRACT);
        return registryAnalysisRepository.save(analysis);
    }

    @Transactional
    public RegistryAnalysisEntity preRegister(PreContractDiagnosisRequest request, MultipartFile snapshot){
        RegistryAnalysisEntity analyze = snapshotAnalysisService.analyze(snapshot);
        analyze.updateDiagnosisType(DiagnosisType.PRECONTRACT);
        analyze.updatePreNickname(request.getNickname());
        return registryAnalysisRepository.save(analyze);
    }

    public List<RegistryAnalysisEntity> getAnalyses(Long userId){
        return registryAnalysisRepository.findAllByUserId(userId)
                .stream()
                .map(AnalysisResponse::from)
                .toList();
    }
}
