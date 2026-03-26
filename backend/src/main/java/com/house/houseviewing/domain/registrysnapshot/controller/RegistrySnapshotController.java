package com.house.houseviewing.domain.registrysnapshot.controller;

import com.house.houseviewing.api.query.service.AnalysisQueryService;
import com.house.houseviewing.api.query.service.DiffAnalysisQueryService;
import com.house.houseviewing.global.file.pdf.dto.PdfDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class RegistrySnapshotController {

    private final AnalysisQueryService analysisQueryService;
    private final DiffAnalysisQueryService diffAnalysisQueryService;

    @PostMapping("/{houseId}/post-contract-diagnoses")
    public ResponseEntity<PdfDownloadResponse> diagnosePostContract(
            @PathVariable Long houseId,
            @RequestPart("file") MultipartFile snapshot){

        PdfDownloadResponse result = analysisQueryService.executePostContractDiagnosis(houseId, snapshot);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{houseId}/change-diagnoses")
    public ResponseEntity<PdfDownloadResponse> diffDiagnose(@PathVariable Long houseId){
        PdfDownloadResponse result = diffAnalysisQueryService.executeDiffDiagnosis(houseId);
        return ResponseEntity.ok(result);
    }
}
