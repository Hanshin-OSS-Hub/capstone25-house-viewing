package com.house.houseviewing.domain.report.postreport.service;

import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.contract.repository.ContractRepository;
import com.house.houseviewing.domain.report.postreport.entity.PostReportEntity;
import com.house.houseviewing.domain.report.postreport.repository.PostReportRepository;
import com.house.houseviewing.domain.analysis.postanalysis.entity.PostAnalysisEntity;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.file.pdf.dto.PdfReportRequest;
import com.house.houseviewing.global.file.pdf.dto.PdfUploadResult;
import com.house.houseviewing.global.file.pdf.service.PdfReportTransferAndReceiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostReportService {

    private final PostReportRepository postReportRepository;
    private final ContractRepository contractRepository;
    private final PdfReportTransferAndReceiveService pdfReportTransferAndReceiveService;

    @Transactional
    public PostReportEntity postRegister(RegistrySnapshotEntity snapshotEntity, PostAnalysisEntity analyze){

        Long contractId = analyze.getContract().getId();
        ContractEntity contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ExceptionCode.CONTRACT_NOT_FOUND));
        PdfReportRequest request = getPdfReportPostRequest(snapshotEntity, analyze, contract);
        PdfUploadResult uploadResult = pdfReportTransferAndReceiveService.transferAndReceive(request);
        PostReportEntity pdfReport = getPdfReportEntity(uploadResult);
        pdfReport.addRegistryAnalysis(analyze);

        return postReportRepository.save(pdfReport);
    }

    @Transactional
    public PostReportEntity preRegister(PostAnalysisEntity analyze){
        PdfReportRequest request = getPdfReportPreRequest(analyze);
        PdfUploadResult uploadResult = pdfReportTransferAndReceiveService.transferAndReceive(request);
        PostReportEntity pdfReport = getPdfReportEntity(uploadResult);

        return postReportRepository.save(pdfReport);
    }

    private static PostReportEntity getPdfReportEntity(PdfUploadResult uploadResult) {
        return PostReportEntity.builder()
                .pdfName(uploadResult.getPdfName())
                .pdfPath(uploadResult.getPdfPath())
                .pdfKey(uploadResult.getPdfKey())
                .pdfSizeBytes(uploadResult.getPdfSizeBytes())
                .build();
    }

    private static PdfReportRequest getPdfReportPostRequest(RegistrySnapshotEntity snapshotEntity, PostAnalysisEntity analyze, ContractEntity contract) {
        return PdfReportRequest.builder()
                .registryAnalysisId(analyze.getId())
                .snapshotName(snapshotEntity.getSnapshotName())
                .rawData(analyze.getRawData())
                .contractType(contract.getContractType())
                .deposit(contract.getDeposit())
                .monthlyAmount(contract.getMonthlyAmount())
                .maintenanceFee(contract.getMaintenanceFee())
                .moveDate(contract.getMoveDate())
                .confirmDate(contract.getConfirmDate())
                .build();
    }

    private static PdfReportRequest getPdfReportPreRequest(PostAnalysisEntity analyze) {
        return PdfReportRequest.builder()
                .registryAnalysisId(analyze.getId())
                .snapshotName(analyze.getSnapshot().getSnapshotName())
                .rawData(analyze.getRawData())
                .build();
    }
}
