package com.house.houseviewing.infrastructure.python.model.pdf;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor @Builder
@NoArgsConstructor @Getter
public class PythonPdfRQ {

    @NotNull(message = "등기부 ID는 필수입니다.")
    private Long snapshotId;

    @NotNull(message = "등기부 JSON은 필수입니다.")
    private String rawData;
}
