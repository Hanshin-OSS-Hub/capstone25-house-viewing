package com.house.houseviewing.domain.analysis.postanalysis.service;

import com.house.houseviewing.domain.common.DiagnosisType;
import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.contract.repository.ContractRepository;
import com.house.houseviewing.domain.analysis.postanalysis.dto.response.AnalysisResponse;
import com.house.houseviewing.domain.analysis.postanalysis.entity.PostAnalysisEntity;
import com.house.houseviewing.domain.analysis.postanalysis.repository.PostAnalysisRepository;
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
public class PostAnalysisService {

    private final ContractRepository contractRepository;
    private final PostAnalysisRepository postAnalysisRepository;
    private final SnapshotAnalysisService snapshotAnalysisService;

    @Transactional
    public PostAnalysisEntity postRegister(MultipartFile snapshot, RegistrySnapshotEntity registrySnapshot){
        Long houseId = registrySnapshot.getHouse().getId();
        ContractEntity contract = contractRepository.findTopByHouseIdOrderByCreatedAtDesc(houseId)
                .orElseThrow(() -> new AppException(ExceptionCode.CONTRACT_NOT_FOUND));
        PostAnalysisEntity analysis = snapshotAnalysisService.analyze(snapshot);
        analysis.addRegistrySnapshot(registrySnapshot);
        analysis.addContract(contract);
        return postAnalysisRepository.save(analysis);
    }

    @Transactional
    public PostAnalysisEntity preRegister(PreContractDiagnosisRequest request, MultipartFile snapshot){
        PostAnalysisEntity analyze = snapshotAnalysisService.analyze(snapshot);
        return postAnalysisRepository.save(analyze);
    }
}
