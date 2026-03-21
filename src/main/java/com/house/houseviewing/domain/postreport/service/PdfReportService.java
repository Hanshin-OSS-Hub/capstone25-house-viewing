package com.house.houseviewing.domain.postreport.service;

import com.house.houseviewing.domain.common.DiagnosisType;
import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.contract.repository.ContractRepository;
import com.house.houseviewing.domain.postreport.entity.PdfReportEntity;
import com.house.houseviewing.domain.postreport.repository.PdfReportRepository;
import com.house.houseviewing.domain.postanalysis.entity.RegistryAnalysisEntity;
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
public class PdfReportService {

    private final PdfReportRepository pdfReportRepository;
    private final ContractRepository contractRepository;
    private final PdfReportTransferAndReceiveService pdfReportTransferAndReceiveService;

    @Transactional
    public PdfReportEntity postRegister(RegistrySnapshotEntity snapshotEntity, RegistryAnalysisEntity analyze){

        Long contractId = analyze.getContract().getId();
        ContractEntity contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ExceptionCode.CONTRACT_NOT_FOUND));
        PdfReportRequest request = getPdfReportPostRequest(snapshotEntity, analyze, contract);
        PdfUploadResult uploadResult = pdfReportTransferAndReceiveService.transferAndReceive(request);
        PdfReportEntity pdfReport = getPdfReportEntity(uploadResult);
        pdfReport.addRegistryAnalysis(analyze);
        pdfReport.updateDiagnosisType(DiagnosisType.POSTCONTRACT);

        return pdfReportRepository.save(pdfReport);
    }

    @Transactional
    public PdfReportEntity preRegister(RegistryAnalysisEntity analyze){
        PdfReportRequest request = getPdfReportPreRequest(analyze);
        PdfUploadResult uploadResult = pdfReportTransferAndReceiveService.transferAndReceive(request);
        PdfReportEntity pdfReport = getPdfReportEntity(uploadResult);
        pdfReport.updateDiagnosisType(DiagnosisType.PRECONTRACT);

        return pdfReportRepository.save(pdfReport);
    }

    private static PdfReportEntity getPdfReportEntity(PdfUploadResult uploadResult) {
        return PdfReportEntity.builder()
                .pdfName(uploadResult.getPdfName())
                .pdfPath(uploadResult.getPdfPath())
                .pdfKey(uploadResult.getPdfKey())
                .pdfSizeBytes(uploadResult.getPdfSizeBytes())
                .build();
    }

    private static PdfReportRequest getPdfReportPostRequest(RegistrySnapshotEntity snapshotEntity, RegistryAnalysisEntity analyze, ContractEntity contract) {
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

    private static PdfReportRequest getPdfReportPreRequest(RegistryAnalysisEntity analyze) {
        return PdfReportRequest.builder()
                .registryAnalysisId(analyze.getId())
                .snapshotName(analyze.getSnapshot().getSnapshotName())
                .rawData(analyze.getRawData())
                .build();
    }
}
