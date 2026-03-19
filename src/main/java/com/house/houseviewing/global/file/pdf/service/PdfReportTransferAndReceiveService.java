package com.house.houseviewing.global.file.pdf.service;

import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.file.pdf.dto.PdfReportRequest;
import com.house.houseviewing.global.file.pdf.dto.PdfUploadResult;
import com.house.houseviewing.infrastructure.python.PythonEngineClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfReportTransferAndReceiveService {

    private final PythonEngineClient pythonEngineClient;
    private final PdfStorageService pdfStorageService;

    public PdfUploadResult transferAndReceive(PdfReportRequest request){
        byte[] pdf = pythonEngineClient.sendDataAndReceivePdf(request).block();

        if(pdf == null || pdf.length == 0){
            throw new AppException(ExceptionCode.PDF_SAVE_FAILED);
        }

        PdfUploadResult uploadPdf = pdfStorageService.uploadPdf(pdf, request.getSnapshotName());

        return uploadPdf;
    }
}
