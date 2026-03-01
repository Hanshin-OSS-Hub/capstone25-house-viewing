package com.house.houseviewing.api.transfer.service;

import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshot.repository.RegistrySnapshotRepository;
import com.house.houseviewing.domain.registrysnapshot.service.RegistrySnapshotService;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.infrastructure.python.PythonEngineClient;
import com.house.houseviewing.infrastructure.python.model.pdf.PythonPdfRQ;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfTransferService {

    private final PythonEngineClient pythonEngineClient;
    private final RegistrySnapshotRepository registrySnapshotRepository;
    private final RegistrySnapshotService registrySnapshotService;

    public void transfer(Long registrySnapshotId){
        RegistrySnapshotEntity registrySnapshot = registrySnapshotRepository.findById(registrySnapshotId)
                .orElseThrow(() -> new AppException(ExceptionCode.FILE_SAVE_FAILED));
        String rawData = registrySnapshot.getRawData();
        PythonPdfRQ request = PythonPdfRQ.builder()
                .rawData(rawData)
                .build();

        pythonEngineClient.sendRawDataAndReceivePdf(request).subscribe(pdfBytes -> {
            String pdfName = "REPORT_" + UUID.randomUUID() + ".pdf";
            String pdfPath = "S3경로" + pdfName;

            try {
                Files.write(Paths.get(pdfPath), pdfBytes); // pdf 저장

            } catch (IOException e){
                log.info("PDF 저장 중 오류가 발생했습니다.", e);
            }
        }, error -> {
            log.error("파이썬 통신 중 에러 발생", error);
        });

    }
}
