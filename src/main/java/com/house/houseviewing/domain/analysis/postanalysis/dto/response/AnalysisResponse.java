package com.house.houseviewing.domain.analysis.postanalysis.dto.response;

import com.house.houseviewing.domain.common.RatePlan;
import com.house.houseviewing.domain.common.RiskLevel;
import com.house.houseviewing.domain.analysis.postanalysis.entity.PostAnalysisEntity;
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

}
