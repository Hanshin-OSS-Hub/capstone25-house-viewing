package com.house.houseviewing.api.upload.service;

import com.house.houseviewing.infrastructure.python.PythonEngineClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PdfUploadService {

    private final PythonEngineClient pythonEngineClient;

    public void upload(MultipartFile file){
        
    }

}
