package com.house.houseviewing.global.file.service;

import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.file.dto.SnapshotUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class SnapshotUploadService {

    private final String uploadPath = "S3 PATH";

    public SnapshotUploadResponse upload(MultipartFile snapshot){
        String snapshotName = snapshot.getOriginalFilename();
        String savedFileName = UUID.randomUUID() + "_" + snapshotName;
        Long snapshotSizeBytes = snapshot.getSize();
        String fullPath = uploadPath + savedFileName;

        try{
            snapshot.transferTo(new File(fullPath));

        } catch (IOException e) {
            throw new AppException(ExceptionCode.FILE_SAVE_FAILED);
        }

        return SnapshotUploadResponse.builder()
                .snapshotName(snapshotName)
                .snapshotUrl(fullPath)
                .snapshotSizeBytes(snapshotSizeBytes)
                .build();
    }
}
