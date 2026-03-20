package com.house.houseviewing.application.registry;

import com.house.houseviewing.domain.pdfreport.entity.PdfReportEntity;
import com.house.houseviewing.domain.pdfreport.service.PdfReportService;
import com.house.houseviewing.domain.registryanalysis.entity.RegistryAnalysisEntity;
import com.house.houseviewing.domain.registryanalysis.service.RegistryAnalysisService;
import com.house.houseviewing.domain.registrysnapshot.dto.request.PreContractDiagnosisRequest;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshot.service.RegistrySnapshotService;
import com.house.houseviewing.global.file.pdf.dto.PdfDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegistryWorkflowService {

    private final RegistrySnapshotService registrySnapshotService;
    private final RegistryAnalysisService registryAnalysisService;
    private final PdfReportService pdfReportService;

    public PdfDownloadResponse executePostContractDiagnosis(Long houseId, MultipartFile snapshot){

        RegistrySnapshotEntity snapshotEntity = registrySnapshotService.postRegister(houseId, snapshot);

        RegistryAnalysisEntity analyze = registryAnalysisService.register(snapshot, snapshotEntity);

        PdfReportEntity pdfReport = pdfReportService.register(snapshotEntity, analyze);

        return PdfDownloadResponse.builder()
                .pdfReportId(pdfReport.getId())
                .filePath(pdfReport.getPdfPath())
                .build();
    }

    public PdfDownloadResponse executePreContractDiagnosis(PreContractDiagnosisRequest request, MultipartFile snapshot){
        RegistrySnapshotEntity snapshotEntity = registrySnapshotService.preRegister(request, snapshot);

    }
}
