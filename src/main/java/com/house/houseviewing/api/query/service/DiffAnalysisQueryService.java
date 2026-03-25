package com.house.houseviewing.api.query.service;

import com.house.houseviewing.domain.analysis.postanalysis.entity.PostAnalysisEntity;
import com.house.houseviewing.domain.analysis.postanalysis.service.PostAnalysisService;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshot.repository.RegistrySnapshotRepository;
import com.house.houseviewing.domain.registrysnapshot.service.RegistrySnapshotService;
import com.house.houseviewing.domain.report.postreport.entity.PostReportEntity;
import com.house.houseviewing.domain.report.postreport.service.PostReportService;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.file.pdf.dto.PdfDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiffAnalysisQueryService {

    private final RegistrySnapshotRepository registrySnapshotRepository;
    private final RegistrySnapshotService registrySnapshotService;
    private final PostAnalysisService postAnalysisService;
    private final PostReportService postReportService;

    public PdfDownloadResponse executeDiffDiagnosis(Long houseId){
        long count = registrySnapshotRepository.countByHouse_Id(houseId) - 1;
        String snapshot = readMockJson(count);

        RegistrySnapshotEntity snapshotEntity = registrySnapshotService.diffRegister(houseId, snapshot);
        PostAnalysisEntity diffAnalysis = postAnalysisService.diffRegister(snapshot, snapshotEntity);
        PostReportEntity pdfReport = postReportService.diffRegister(snapshotEntity, diffAnalysis);

        return PdfDownloadResponse.builder()
                .pdfReportId(pdfReport.getId())
                .filePath(pdfReport.getPdfPath())
                .build();
    }

    public String readMockJson(long count){
        String fileName;
        if (count == 0) {
            fileName = "SAFE-registry.json";
        } else if (count == 1) {
            fileName = "WARNING-registry.json";
        } else {
            fileName = "DANGER-registry.json";
        }
        try {
            ClassPathResource resource = new ClassPathResource("infrastructure/mock/registry/" + fileName);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e){
            throw new AppException(ExceptionCode.MOCK_NOT_FOUND);
        }
    }
}
