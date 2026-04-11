package com.house.houseviewing.global.file.pdf.service;

import com.house.houseviewing.global.file.pdf.dto.PdfUploadResult;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PdfStorageService {

    public PdfUploadResult uploadPdf(byte[] pdfBytes, String originName){

        String pdfKey = "pdf/" + UUID.randomUUID() + "_" + originName;
        String pdfPath = "https://s3-bucket-url/" + pdfKey;
        Long pdfSizeBytes = (long) pdfBytes.length;

        // S3 업로드 로직

        return PdfUploadResult.builder()
                .pdfSizeBytes(pdfSizeBytes)
                .pdfName(originName)
                .pdfKey(pdfKey)
                .pdfPath(pdfPath)
                .build();
    }
}
