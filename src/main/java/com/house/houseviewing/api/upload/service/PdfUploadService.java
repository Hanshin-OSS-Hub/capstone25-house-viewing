package com.house.houseviewing.api.upload.service;

import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshot.repository.RegistrySnapshotRepository;
import com.house.houseviewing.domain.registrysnapshot.service.RegistrySnapshotService;
import com.house.houseviewing.infrastructure.python.PythonEngineClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfUploadService {

    private final PythonEngineClient pythonEngineClient;
    private final RegistrySnapshotService registrySnapshotService;

    public void upload(MultipartFile file){

        pythonEngineClient.sendPdf(file).subscribe(response -> {
            registrySnapshotService.register(response, file.getOriginalFilename());
        });
    }

}
