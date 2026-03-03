package com.house.houseviewing.api.upload.service;

import com.house.houseviewing.api.transfer.service.PdfTransferService;
import com.house.houseviewing.domain.registrysnapshot.service.RegistrySnapshotService;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.infrastructure.python.PythonEngineClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfUploadService {

    private final PythonEngineClient pythonEngineClient;
    private final RegistrySnapshotService registrySnapshotService;
    private final PdfTransferService pdfTransferService;

    private final String uploadPath = "S3 PATH";

    public void upload(MultipartFile file){
        String originFileName = file.getOriginalFilename();
        String s3FileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String fullPath = uploadPath + s3FileName;

        try{
            file.transferTo(new File(fullPath)); // 등기부 저장

            pythonEngineClient.sendPdf(file).subscribe(response -> {
                Long register = registrySnapshotService.register(response, originFileName, fullPath);
                pdfTransferService.transfer(register);
            });
        } catch (IOException e){
            log.info("파일 저장 실패: {}", e.getMessage());
            throw new AppException(ExceptionCode.FILE_SAVE_FAILED);
        }
    }
}
