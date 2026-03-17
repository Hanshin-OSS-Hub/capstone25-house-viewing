package com.house.houseviewing.global.file.service;

import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshot.service.RegistrySnapshotService;
import com.house.houseviewing.global.file.dto.SnapshotUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SnapshotExtractService {

    private final RegistrySnapshotService registrySnapshotService;
    private final SnapshotUploadService snapshotUploadService;

    public RegistrySnapshotEntity register(MultipartFile snapshot) {

        SnapshotUploadResponse response = snapshotUploadService.upload(snapshot);

        return RegistrySnapshotEntity.builder()
                .snapshotName(response.getSnapshotName())
                .snapshotUrl(response.getSnapshotUrl())
                .snapshotSizeBytes(response.getSnapshotSizeBytes())
                .build();
    }
}
