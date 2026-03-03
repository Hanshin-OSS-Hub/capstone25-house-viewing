package com.house.houseviewing.domain.pdfreport.service;

import com.house.houseviewing.domain.pdfreport.entity.PdfReportEntity;
import com.house.houseviewing.domain.pdfreport.model.register.PdfReportRegisterRS;
import com.house.houseviewing.domain.pdfreport.repository.PdfReportRepository;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshot.repository.RegistrySnapshotRepository;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PdfReportService {

    private final PdfReportRepository pdfReportRepository;
    private final RegistrySnapshotRepository registrySnapshotRepository;

    @Transactional
    public Long register(PdfReportRegisterRS response){

        RegistrySnapshotEntity registrySnapshot = registrySnapshotRepository.findById(response.getSnapshotId())
                .orElseThrow(() -> new AppException(ExceptionCode.SNAPSHOT_NOT_FOUND));
        PdfReportEntity pdfReport = PdfReportEntity.builder()
                .pdfName(response.getPdfName())
                .pdfPath(response.getPdfPath())
                .build();
        pdfReport.addRegistrySnapshot(registrySnapshot);
        PdfReportEntity save = pdfReportRepository.save(pdfReport);
        return save.getId();
    }
}
