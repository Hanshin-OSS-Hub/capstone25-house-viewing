package com.house.houseviewing.infrastructure.s3;

import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file){
        String s3FileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        try{
            S3Resource resource = s3Template.upload(bucket, s3FileName, file.getInputStream(),
                    ObjectMetadata.builder().contentType(file.getContentType()).build());
            return resource.getURL().toString();
        } catch (IOException e){
            throw new AppException(ExceptionCode.S3_UPLOAD_FAILED);
        }
    }
}
