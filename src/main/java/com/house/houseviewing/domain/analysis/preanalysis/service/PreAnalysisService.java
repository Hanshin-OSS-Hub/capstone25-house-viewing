package com.house.houseviewing.domain.analysis.preanalysis.service;

import com.house.houseviewing.domain.analysis.postanalysis.dto.response.AnalysisResponse;
import com.house.houseviewing.domain.analysis.postanalysis.entity.PostAnalysisEntity;
import com.house.houseviewing.domain.analysis.preanalysis.entity.PreAnalysisEntity;
import com.house.houseviewing.domain.analysis.preanalysis.repository.PreAnalysisRepository;
import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.common.RatePlan;
import com.house.houseviewing.domain.registrysnapshot.dto.request.PreContractDiagnosisRequest;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.repository.UserRepository;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.external.kakao.service.KakaoAddress;
import com.house.houseviewing.global.file.snapshot.service.SnapshotAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PreAnalysisService {

    private final UserRepository userRepository;
    private final SnapshotAnalysisService snapshotAnalysisService;
    private final PreAnalysisRepository preAnalysisRepository;
    private final KakaoAddress kakaoAddress;

    @Transactional
    public PreAnalysisEntity preRegister(Long userId, PreContractDiagnosisRequest request, MultipartFile snapshot){
        UserEntity user = userRepository.findById(userId).
                orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));

        Address address = kakaoAddress.parsingAddress(request.getAddress());
        PreAnalysisEntity analyze = snapshotAnalysisService.preAnalyze(request.getNickname(), address, snapshot);
        analyze.addUser(user);
        return preAnalysisRepository.save(analyze);
    }

    private boolean isFreePlan(UserEntity user){
        if(user.getRatePlan() == RatePlan.FREE){
            return true;
        }
        else
            return false;
    }

    public List<AnalysisResponse> getPreAnalyses(Long userId){
        List<PreAnalysisEntity> preAnalyses = preAnalysisRepository.findAllByUserId(userId);
        return preAnalyses
                .stream()
                .map(AnalysisResponse::from)
                .toList();
    }
}
