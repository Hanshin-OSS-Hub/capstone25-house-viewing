package com.house.houseviewing.application.registry;

import com.house.houseviewing.domain.postcontractpdfreports.entity.PdfReportEntity;
import com.house.houseviewing.domain.postcontractpdfreports.service.PdfReportService;
import com.house.houseviewing.domain.postcontractanalyses.entity.RegistryAnalysisEntity;
import com.house.houseviewing.domain.postcontractanalyses.service.RegistryAnalysisService;
import com.house.houseviewing.domain.registrysnapshots.dto.request.PreContractDiagnosisRequest;
import com.house.houseviewing.domain.registrysnapshots.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshots.service.RegistrySnapshotService;
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
