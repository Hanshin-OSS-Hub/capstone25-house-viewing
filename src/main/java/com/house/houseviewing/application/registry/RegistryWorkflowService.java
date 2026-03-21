package com.house.houseviewing.application.registry;

import com.house.houseviewing.domain.postreport.entity.PdfReportEntity;
import com.house.houseviewing.domain.postreport.service.PdfReportService;
import com.house.houseviewing.domain.postanalysis.entity.RegistryAnalysisEntity;
import com.house.houseviewing.domain.postanalysis.service.RegistryAnalysisService;
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
        RegistryAnalysisEntity analyze = registryAnalysisService.postRegister(snapshot, snapshotEntity);
        PdfReportEntity pdfReport = pdfReportService.postRegister(snapshotEntity, analyze);

        return PdfDownloadResponse.builder()
                .pdfReportId(pdfReport.getId())
                .filePath(pdfReport.getPdfPath())
                .build();
    }

    public PdfDownloadResponse executePreContractDiagnosis(PreContractDiagnosisRequest request, MultipartFile snapshot){
        RegistryAnalysisEntity analyze = registryAnalysisService.preRegister(request, snapshot);
        PdfReportEntity pdfReport = pdfReportService.preRegister(analyze);

        return PdfDownloadResponse.builder()
                .pdfReportId(pdfReport.getId())
                .filePath(pdfReport.getPdfPath())
                .build();
    }
}
