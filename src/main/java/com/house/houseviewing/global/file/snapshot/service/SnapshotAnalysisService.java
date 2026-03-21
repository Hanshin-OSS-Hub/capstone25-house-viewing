package com.house.houseviewing.global.file.snapshot.service;

import com.house.houseviewing.domain.postanalysis.entity.RegistryAnalysisEntity;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.file.snapshot.dto.SnapshotAnalysisResult;
import com.house.houseviewing.infrastructure.python.PythonEngineClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SnapshotAnalysisService {

    private final PythonEngineClient pythonEngineClient;

    public RegistryAnalysisEntity analyze(MultipartFile snapshot){

        try{
            SnapshotAnalysisResult result = pythonEngineClient.sendPdf(snapshot).block();

            if(result == null){
                throw new AppException(ExceptionCode.ANALYSIS_FAILED);
            }

            return RegistryAnalysisEntity.builder()
                    .rawData(result.getRawData())
                    .mainReason(result.getMainReason())
                    .ltvScore(result.getLtvScore())
                    .riskLevel(result.getRiskLevel())
                    .build();
        } catch (Exception e){
            throw new AppException(ExceptionCode.ANALYSIS_FAILED);
        }
    }
}
