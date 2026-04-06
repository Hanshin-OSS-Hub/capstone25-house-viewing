package com.house.houseviewing.domain.analysis.preanalysis.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PreContractDiagnosisRequest {

    private String nickname;

    private String address;
}
