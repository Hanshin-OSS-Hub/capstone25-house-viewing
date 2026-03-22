package com.house.houseviewing.domain.analysis.preanalysis.service;

import com.house.houseviewing.domain.analysis.postanalysis.entity.PostAnalysisEntity;
import com.house.houseviewing.domain.analysis.preanalysis.entity.PreAnalysisEntity;
import com.house.houseviewing.domain.analysis.preanalysis.repository.PreAnalysisRepository;
import com.house.houseviewing.domain.registrysnapshot.dto.request.PreContractDiagnosisRequest;
import com.house.houseviewing.global.file.snapshot.service.SnapshotAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PreAnalysisService {

    private final SnapshotAnalysisService snapshotAnalysisService;
    private final PreAnalysisRepository preAnalysisRepository;

    @Transactional
    public PreAnalysisEntity preRegister(PreContractDiagnosisRequest request, MultipartFile snapshot){
        PreAnalysisEntity analyze = snapshotAnalysisService.preAnalyze(request.getNickname(), snapshot);
        analyze.updateRatePlan();
        return preAnalysisRepository.save(analyze);
    }
}
