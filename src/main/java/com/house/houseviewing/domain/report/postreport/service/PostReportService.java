package com.house.houseviewing.domain.report.postreport.service;

import com.house.houseviewing.domain.analysis.postanalysis.repository.PostAnalysisRepository;
import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.contract.repository.ContractRepository;
import com.house.houseviewing.domain.report.postreport.entity.PostReportEntity;
import com.house.houseviewing.domain.report.postreport.repository.PostReportRepository;
import com.house.houseviewing.domain.analysis.postanalysis.entity.PostAnalysisEntity;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.file.pdf.dto.PdfDiffReportRequest;
import com.house.houseviewing.global.file.pdf.dto.PdfPostReportRequest;
import com.house.houseviewing.global.file.pdf.dto.PdfUploadResult;
import com.house.houseviewing.global.file.pdf.service.PdfReportTransferAndReceiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostReportService {

    private final PostReportRepository postReportRepository;
    private final ContractRepository contractRepository;
    private final PdfReportTransferAndReceiveService pdfReportTransferAndReceiveService;
    private final PostAnalysisRepository postAnalysisRepository;

    @Transactional
    public PostReportEntity postRegister(PostAnalysisEntity analyze){
        Long contractId = analyze.getContract().getId();
        ContractEntity contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ExceptionCode.CONTRACT_NOT_FOUND));
        PdfPostReportRequest request = getPdfReportPostRequest(snapshotEntity, analyze, contract);
        PdfUploadResult uploadResult = pdfReportTransferAndReceiveService.postTransferAndReceive(request);
        PostReportEntity pdfReport = getPdfReportEntity(uploadResult);
        pdfReport.addRegistryAnalysis(analyze);

        return postReportRepository.save(pdfReport);
    }

    @Transactional
    public PostReportEntity diffRegister(RegistrySnapshotEntity snapshotEntity, PostAnalysisEntity analyze){
        Long houseId = analyze.getContract().getHouse().getId();
        Long contractId = analyze.getContract().getId();
        ContractEntity contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ExceptionCode.CONTRACT_NOT_FOUND));
        String originData = getOriginData(houseId);
        PdfDiffReportRequest request = getPdfDiffReportRequest(originData, snapshotEntity, analyze, contract);
        PdfUploadResult uploadResult = pdfReportTransferAndReceiveService.diffTransferAndReceive(request);
        PostReportEntity pdfReport = getPdfReportEntity(uploadResult);
        pdfReport.addRegistryAnalysis(analyze);

        return postReportRepository.save(pdfReport);
    }

    private String getOriginData(Long houseId) {
        List<PostAnalysisEntity> list = postAnalysisRepository.findTop2ByContractHouseIdOrderByCreatedAtDesc(houseId);
        PostAnalysisEntity analysisEntity = list.get(0);
        String originData = analysisEntity.getRawData();
        return originData;
    }

    private static PostReportEntity getPdfReportEntity(PdfUploadResult uploadResult) {
        return PostReportEntity.builder()
                .pdfName(uploadResult.getPdfName())
                .pdfPath(uploadResult.getPdfPath())
                .pdfKey(uploadResult.getPdfKey())
                .pdfSizeBytes(uploadResult.getPdfSizeBytes())
                .build();
    }

    private static PdfPostReportRequest getPdfReportPostRequest(RegistrySnapshotEntity snapshotEntity, PostAnalysisEntity analyze, ContractEntity contract) {
        return PdfPostReportRequest.builder()
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

    private static PdfDiffReportRequest getPdfDiffReportRequest(String originData, RegistrySnapshotEntity snapshotEntity, PostAnalysisEntity analysis, ContractEntity contract){
        return PdfDiffReportRequest.builder()
                .snapshotName(snapshotEntity.getSnapshotName())
                .originData(originData)
                .newData(analysis.getRawData())
                .contractType(contract.getContractType())
                .deposit(contract.getDeposit())
                .monthlyAmount(contract.getMonthlyAmount())
                .maintenanceFee(contract.getMaintenanceFee())
                .moveDate(contract.getMoveDate())
                .confirmDate(contract.getConfirmDate())
                .build();
    }
}