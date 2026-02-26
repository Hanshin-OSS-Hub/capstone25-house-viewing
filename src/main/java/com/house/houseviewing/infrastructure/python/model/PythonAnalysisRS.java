package com.house.houseviewing.infrastructure.python.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor @Builder
@NoArgsConstructor @Getter
public class PythonAnalysisRS {

    private Integer ltvScore;

    private String rawData;
}
