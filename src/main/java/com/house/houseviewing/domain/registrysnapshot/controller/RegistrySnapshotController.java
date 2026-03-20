package com.house.houseviewing.domain.registrysnapshot.controller;

import com.house.houseviewing.application.registry.RegistryWorkflowService;
import com.house.houseviewing.domain.registrysnapshot.dto.request.PreContractDiagnosisRequest;
import com.house.houseviewing.domain.registrysnapshot.dto.response.SnapshotResultResponse;
import com.house.houseviewing.domain.registrysnapshot.service.RegistrySnapshotService;
import com.house.houseviewing.global.file.pdf.dto.PdfDownloadResponse;
import com.house.houseviewing.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/snapshot")
@RequiredArgsConstructor
public class RegistrySnapshotController {

    private final RegistrySnapshotService registrySnapshotService;
    private final RegistryWorkflowService registryWorkflowService;

    @PostMapping("/houses/{houseId}/post-contract-diagnoses")
    public ResponseEntity<PdfDownloadResponse> diagnosePostContract(
            @PathVariable Long houseId,
            @RequestPart("file") MultipartFile snapshot){

        PdfDownloadResponse result = registryWorkflowService.executePostContractDiagnosis(houseId, snapshot);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/pre-contract-diagnoses")
    public ResponseEntity<PdfDownloadResponse> diagnosePreContract(
            @RequestPart("data") PreContractDiagnosisRequest request,
            @RequestPart("file") MultipartFile snapshot
    ){
        registryWorkflowService.executePreContractDiagnosis(request, snapshot);
    }

    @GetMapping
    public ResponseEntity<List<SnapshotResultResponse>> getResults(
            @AuthenticationPrincipal CustomUserDetails userDetails){
        List<SnapshotResultResponse> result = registrySnapshotService.getSnapshots(userDetails.getUserId());
        return ResponseEntity.ok(result);
    }
}
