package com.house.houseviewing.domain.registrysnapshot.controller;

import com.house.houseviewing.api.query.service.AnalysisQueryService;
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

    @PostMapping("/{houseId}/post-contract-diagnoses")
    public ResponseEntity<PdfDownloadResponse> diagnosePostContract(
            @PathVariable Long houseId,
            @RequestPart("file") MultipartFile snapshot){

        PdfDownloadResponse result = analysisQueryService.executePostContractDiagnosis(houseId, snapshot);
        return ResponseEntity.ok(result);
    }
}
