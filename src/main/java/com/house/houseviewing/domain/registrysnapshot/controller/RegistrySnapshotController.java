package com.house.houseviewing.domain.registrysnapshot.controller;

import com.house.houseviewing.application.registry.RegistryWorkflowService;
import com.house.houseviewing.domain.registrysnapshot.dto.request.PreContractDiagnosisRequest;
import com.house.houseviewing.global.file.pdf.dto.PdfDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/snapshot")
@RequiredArgsConstructor
public class RegistrySnapshotController {

    private final RegistryWorkflowService registryWorkflowService;

    @PostMapping("/houses/{houseId}/post-contract-diagnoses")
    public ResponseEntity<PdfDownloadResponse> diagnosePostContract(
            @PathVariable Long houseId,
            @RequestPart("file") MultipartFile snapshot){

        PdfDownloadResponse result = registryWorkflowService.executePostContractDiagnosis(houseId, snapshot);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/houses/pre-contract-diganoses")
    public ResponseEntity<PdfDownloadResponse> diagnosePreContract(
            @RequestPart("file") MultipartFile snapshot,
            @RequestPart("data")PreContractDiagnosisRequest request){

        PdfDownloadResponse result = registryWorkflowService.executePreContractDiagnosis(request, snapshot);
        return ResponseEntity.ok(result);
    }
}
