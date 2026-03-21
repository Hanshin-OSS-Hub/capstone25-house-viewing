package com.house.houseviewing.domain.postcontractanalyses.dto.response;

import com.house.houseviewing.domain.common.DiagnosisType;
import com.house.houseviewing.domain.common.RiskLevel;
import com.house.houseviewing.domain.postcontractanalyses.entity.RegistryAnalysisEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalysisResponse {

    private String nickname;

    private String address;

    private String mainReason;

    private RiskLevel riskLevel;

    private Integer ltvScore;

    public static AnalysisResponse from(RegistryAnalysisEntity registryAnalysis){
        String nickname;
        if(registryAnalysis.getDiagnosisType() == DiagnosisType.PRECONTRACT){
            nickname = registryAnalysis.getPreNickname();
        }
        else{
            nickname =
        }
    }
}
