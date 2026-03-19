package com.house.houseviewing.global.file.pdf.dto;

import com.house.houseviewing.domain.pdfreport.entity.PdfReportEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor @Builder
@NoArgsConstructor @Getter
public class PdfUploadResult {

    private String pdfKey;

    private String pdfName;

    private String pdfPath;

    private Long pdfSizeBytes;

}
