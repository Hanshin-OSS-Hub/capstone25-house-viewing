package com.house.houseviewing.application.registry;

import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.contract.repository.ContractRepository;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.repository.HouseRepository;
import com.house.houseviewing.domain.pdfreport.entity.PdfReportEntity;
import com.house.houseviewing.domain.pdfreport.repository.PdfReportRepository;
import com.house.houseviewing.domain.registryanalysis.entity.RegistryAnalysisEntity;
import com.house.houseviewing.domain.registryanalysis.repository.RegistryAnalysisRepository;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshot.repository.RegistrySnapshotRepository;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.file.pdf.dto.PdfReportRequest;
import com.house.houseviewing.global.file.pdf.dto.PdfUploadResult;
import com.house.houseviewing.global.file.pdf.service.PdfReportTransferAndReceiveService;
import com.house.houseviewing.global.file.snapshot.service.SnapshotAnalysisService;
import com.house.houseviewing.global.file.snapshot.service.SnapshotExtractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegistryWorkflowService {

    private final HouseRepository houseRepository;
    private final ContractRepository contractRepository;
    private final RegistrySnapshotRepository registrySnapshotRepository;
    private final RegistryAnalysisRepository registryAnalysisRepository;
    private final PdfReportRepository pdfReportRepository;
    private final SnapshotExtractService snapshotExtractService;
    private final SnapshotAnalysisService snapshotAnalysisService;
    private final PdfReportTransferAndReceiveService pdfReportTransferAndReceiveService;


    @Transactional
    public void register(Long houseId, MultipartFile snapshot){
        HouseEntity house = houseRepository.findById(houseId)
                .orElseThrow(() -> new AppException(ExceptionCode.HOUSE_NOT_FOUND));
        ContractEntity contract = contractRepository.findTopByHouseIdOrderByCreatedAtDesc(houseId)
                .orElseThrow(() -> new AppException(ExceptionCode.CONTRACT_NOT_FOUND));

        RegistrySnapshotEntity snapshotEntity = snapshotExtractService.register(snapshot);
        registrySnapshotRepository.save(snapshotEntity);
        house.addRegistrySnapshot(snapshotEntity);

        RegistryAnalysisEntity analyze = snapshotAnalysisService.analyze(snapshot);
        registryAnalysisRepository.save(analyze);
        analyze.addRegistrySnapshot(snapshotEntity);
        analyze.addContract(contract);

        PdfReportRequest request = getPdfReportRequest(snapshotEntity, analyze, contract);
        PdfUploadResult uploadResult = pdfReportTransferAndReceiveService.transferAndReceive(request);
        PdfReportEntity pdfReport = getPdfReportEntity(uploadResult);
        pdfReportRepository.save(pdfReport);
        pdfReport.addRegistryAnalysis(analyze);

    }

    private static PdfReportEntity getPdfReportEntity(PdfUploadResult uploadResult) {
        return PdfReportEntity.builder()
                .pdfName(uploadResult.getPdfName())
                .pdfPath(uploadResult.getPdfPath())
                .pdfKey(uploadResult.getPdfKey())
                .pdfSizeBytes(uploadResult.getPdfSizeBytes())
                .build();
    }

    private static PdfReportRequest getPdfReportRequest(RegistrySnapshotEntity snapshotEntity, RegistryAnalysisEntity analyze, ContractEntity contract) {
        return PdfReportRequest.builder()
                .registryAnalysisId(snapshotEntity.getId())
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
}
