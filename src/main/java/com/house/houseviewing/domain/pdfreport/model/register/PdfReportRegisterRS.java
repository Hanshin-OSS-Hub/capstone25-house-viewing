package com.house.houseviewing.domain.pdfreport.model.register;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class PdfReportRegisterRS {

    private Long snapshotId;

    private String pdfName;

    private String pdfPath;

}
