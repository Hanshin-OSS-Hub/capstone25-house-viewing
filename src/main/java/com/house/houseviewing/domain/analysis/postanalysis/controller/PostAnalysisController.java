package com.house.houseviewing.domain.analysis.postanalysis.controller;

import com.house.houseviewing.api.query.service.DiffAnalysisQueryService;
import com.house.houseviewing.global.file.pdf.dto.PdfDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class PostAnalysisController {

    private final DiffAnalysisQueryService diffAnalysisQueryService;

    @PostMapping("/{houseId}}/change-diagnoses")
    public ResponseEntity<PdfDownloadResponse> diffDiagnose(@PathVariable Long houseId){
        PdfDownloadResponse result = diffAnalysisQueryService.executeDiffDiagnosis(houseId);
        return ResponseEntity.ok(result);
    }
}
