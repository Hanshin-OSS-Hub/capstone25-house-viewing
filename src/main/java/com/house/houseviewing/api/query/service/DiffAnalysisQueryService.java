package com.house.houseviewing.api.query.service;

import com.house.houseviewing.domain.analysis.postanalysis.entity.PostAnalysisEntity;
import com.house.houseviewing.domain.analysis.postanalysis.service.PostAnalysisService;
import com.house.houseviewing.domain.report.postreport.entity.PostReportEntity;
import com.house.houseviewing.domain.report.postreport.service.PostReportService;
import com.house.houseviewing.global.file.pdf.dto.PdfDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiffAnalysisQueryService {

    private final PostAnalysisService postAnalysisService;
    private final PostReportService postReportService;

    public PdfDownloadResponse executeDiffDiagnosis(Long houseId){
        PostAnalysisEntity diffAnalysis = postAnalysisService.diffRegister(houseId);
        PostReportEntity pdfReport = postReportService.diffRegister(diffAnalysis);

        return PdfDownloadResponse.builder()
                .pdfReportId(pdfReport.getId())
                .filePath(pdfReport.getPdfPath())
                .build();
    }
}
