package com.house.houseviewing.domain.analysis.preanalysis.controller;

import com.house.houseviewing.application.analyze.AnalysisWorkflowService;
import com.house.houseviewing.domain.registrysnapshot.dto.request.PreContractDiagnosisRequest;
import com.house.houseviewing.global.file.pdf.dto.PdfDownloadResponse;
import com.house.houseviewing.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class PreAnalysisController {

    private final AnalysisWorkflowService analysisWorkflowService;

    @PostMapping("/pre-contract-diganoses")
    public ResponseEntity<PdfDownloadResponse> diagnosePreContract(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("file") MultipartFile snapshot,
            @RequestPart("data") PreContractDiagnosisRequest request){

        PdfDownloadResponse result = analysisWorkflowService.executePreContractDiagnosis(userDetails.getUserId(), request, snapshot);
        return ResponseEntity.ok(result);
    }
}
