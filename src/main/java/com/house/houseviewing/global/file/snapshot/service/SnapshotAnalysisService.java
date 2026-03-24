package com.house.houseviewing.global.file.snapshot.service;

import com.house.houseviewing.domain.analysis.postanalysis.entity.PostAnalysisEntity;
import com.house.houseviewing.domain.analysis.preanalysis.entity.PreAnalysisEntity;
import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.file.snapshot.dto.SnapshotPostAnalysisResult;
import com.house.houseviewing.global.file.snapshot.dto.SnapshotPreAnalysisResult;
import com.house.houseviewing.infrastructure.python.PythonEngineClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SnapshotAnalysisService {

    private final PythonEngineClient pythonEngineClient;

    public PostAnalysisEntity postAnalyze(MultipartFile snapshot){

        try{
            SnapshotPostAnalysisResult result = pythonEngineClient.sendPostPdf(snapshot).block();

            if(result == null){
                throw new AppException(ExceptionCode.ANALYSIS_FAILED);
            }

            return PostAnalysisEntity.builder()
                    .rawData(result.getRawData())
                    .mainReason(result.getMainReason())
                    .ltvScore(result.getLtvScore())
                    .riskLevel(result.getRiskLevel())
                    .build();
        } catch (Exception e){
            throw new AppException(ExceptionCode.ANALYSIS_FAILED);
        }
    }

    public PreAnalysisEntity preAnalyze(String nickname, Address address, MultipartFile snapshot){

        try{
            SnapshotPreAnalysisResult result = pythonEngineClient.sendPrePdf(snapshot).block();

            if(result == null){
                throw new AppException(ExceptionCode.ANALYSIS_FAILED);
            }

            return PreAnalysisEntity.builder()
                    .nickname(nickname)
                    .rawData(result.getRawData())
                    .mainReason(result.getMainReason())
                    .address(address)
                    .ltvScore(result.getLtvScore())
                    .riskLevel(result.getRiskLevel())
                    .build();
        } catch (Exception e){
            throw new AppException(ExceptionCode.ANALYSIS_FAILED);
        }
    }
}
