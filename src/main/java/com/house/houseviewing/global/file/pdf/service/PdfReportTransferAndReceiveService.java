package com.house.houseviewing.global.file.pdf.service;

import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.file.pdf.dto.PdfReportRequest;
import com.house.houseviewing.infrastructure.python.PythonEngineClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfReportTransferAndReceiveService {

    private final PythonEngineClient pythonEngineClient;
    private final PdfStorageService pdfStorageService;

    public void transferAndReceive(PdfReportRequest request){
        byte[] pdf = pythonEngineClient.sendDataAndReceivePdf(request).block();

        if(pdf == null || pdf.length == 0){
            throw new AppException(ExceptionCode.PDF_SAVE_FAILED);
        }


    }
}
