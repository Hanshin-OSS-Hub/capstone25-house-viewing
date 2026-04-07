package com.house.houseviewing.infrastructure.s3;

import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.file.pdf.dto.PdfUploadResult;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public PdfUploadResult pdfUpload(byte[] pdf){
        String s3FileName = "analysis_" + UUID.randomUUID() + "originFileName" + ".pdf";
        try{
            S3Resource resource = s3Template.upload(bucket, s3FileName, new ByteArrayInputStream(pdf),
                    ObjectMetadata.builder().contentType("application/pdf").build());
            return PdfUploadResult.builder()
                    .pdfKey(s3FileName)
                    .pdfPath(resource.getURL().toString())
                    .pdfSizeBytes((long) pdf.length)
                    .pdfName("안전_진단_리포트.pdf")
                    .build();
        } catch (IOException e){
            throw new AppException(ExceptionCode.S3_UPLOAD_FAILED);
        }
    }
}
