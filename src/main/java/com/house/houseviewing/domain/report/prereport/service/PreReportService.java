package com.house.houseviewing.domain.report.prereport.service;

import com.house.houseviewing.domain.analysis.postanalysis.entity.PostAnalysisEntity;
import com.house.houseviewing.domain.analysis.preanalysis.entity.PreAnalysisEntity;
import com.house.houseviewing.domain.report.postreport.entity.PostReportEntity;
import com.house.houseviewing.domain.report.prereport.entity.PreReportEntity;
import com.house.houseviewing.domain.report.prereport.repository.PreReportRepository;
import com.house.houseviewing.global.file.pdf.dto.PdfReportRequest;
import com.house.houseviewing.global.file.pdf.dto.PdfUploadResult;
import com.house.houseviewing.global.file.pdf.service.PdfReportTransferAndReceiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PreReportService {

    private final PreReportRepository preReportRepository;
    private final PdfReportTransferAndReceiveService pdfReportTransferAndReceiveService;

    @Transactional
    public PreReportEntity preRegister(PreAnalysisEntity analyze){
        PdfReportRequest request = getPdfReportPreRequest(analyze);
        PdfUploadResult uploadResult = pdfReportTransferAndReceiveService.transferAndReceive(request);
        PreReportEntity pdfReport = getPdfReportEntity(uploadResult);

        return preReportRepository.save(pdfReport);
    }

    private static PdfReportRequest getPdfReportPreRequest(PreAnalysisEntity analyze) {
        return PdfReportRequest.builder()
                .registryAnalysisId(analyze.getId())
                .snapshotName(analyze.getNickname())
                .rawData(analyze.getRawData())
                .build();
    }

    private static PreReportEntity getPdfReportEntity(PdfUploadResult uploadResult) {
        return PreReportEntity.builder()
                .pdfName(uploadResult.getPdfName())
                .pdfPath(uploadResult.getPdfPath())
                .pdfKey(uploadResult.getPdfKey())
                .pdfSizeBytes(uploadResult.getPdfSizeBytes())
                .build();
    }
}
