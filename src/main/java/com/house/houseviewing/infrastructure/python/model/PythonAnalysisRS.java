package com.house.houseviewing.infrastructure.python.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor @Builder
@NoArgsConstructor
public class PythonAnalysisRS {

    private Integer ltvScore;

    private String rawData;
}
