package com.house.houseviewing.domain.analysis.preanalysis.controller;

import com.house.houseviewing.application.registry.RegistryWorkflowService;
import com.house.houseviewing.domain.analysis.preanalysis.service.PreAnalysisService;
import com.house.houseviewing.domain.registrysnapshot.dto.request.PreContractDiagnosisRequest;
import com.house.houseviewing.global.file.pdf.dto.PdfDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class PreAnalysisController {

    private final RegistryWorkflowService registryWorkflowService;

    @PostMapping("/pre-contract-diganoses")
    public ResponseEntity<PdfDownloadResponse> diagnosePreContract(
            @RequestPart("file") MultipartFile snapshot,
            @RequestPart("data") PreContractDiagnosisRequest request){

        PdfDownloadResponse result = registryWorkflowService.executePreContractDiagnosis(request, snapshot);
        return ResponseEntity.ok(result);
    }
}
