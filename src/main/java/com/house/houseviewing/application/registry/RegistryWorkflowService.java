package com.house.houseviewing.application.registry;

import com.house.houseviewing.domain.report.postreport.entity.PostReportEntity;
import com.house.houseviewing.domain.report.postreport.service.PostReportService;
import com.house.houseviewing.domain.analysis.postanalysis.entity.PostAnalysisEntity;
import com.house.houseviewing.domain.analysis.postanalysis.service.PostAnalysisService;
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
    private final PostAnalysisService postAnalysisService;
    private final PostReportService postReportService;

    public PdfDownloadResponse executePostContractDiagnosis(Long houseId, MultipartFile snapshot){

        RegistrySnapshotEntity snapshotEntity = registrySnapshotService.register(houseId, snapshot);
        PostAnalysisEntity analyze = postAnalysisService.postRegister(snapshot, snapshotEntity);
        PostReportEntity pdfReport = postReportService.postRegister(snapshotEntity, analyze);

        return PdfDownloadResponse.builder()
                .pdfReportId(pdfReport.getId())
                .filePath(pdfReport.getPdfPath())
                .build();
    }

    public PdfDownloadResponse executePreContractDiagnosis(PreContractDiagnosisRequest request, MultipartFile snapshot){
        PostAnalysisEntity analyze = postAnalysisService.preRegister(request, snapshot);
        PostReportEntity pdfReport = postReportService.preRegister(analyze);

        return PdfDownloadResponse.builder()
                .pdfReportId(pdfReport.getId())
                .filePath(pdfReport.getPdfPath())
                .build();
    }
}
