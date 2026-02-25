package com.house.houseviewing.api.upload.controller;

import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class PdfUploadController {

    @PostMapping("/api/upload/pdf")
    public ResponseEntity<Void> upload(@RequestParam("file")MultipartFile file){
        if(file.isEmpty()){
            throw new AppException(ExceptionCode.VERIFY_FILE_FAILED);
        }


    }
}
