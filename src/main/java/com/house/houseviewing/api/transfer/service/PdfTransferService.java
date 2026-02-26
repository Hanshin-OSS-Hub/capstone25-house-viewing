package com.house.houseviewing.api.transfer.service;

import com.house.houseviewing.infrastructure.python.PythonEngineClient;
import com.house.houseviewing.infrastructure.python.model.pdf.PythonPdfRQ;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfTransferService {

    private final PythonEngineClient pythonEngineClient;

    public void transfer(PythonPdfRQ request){

    }
}
