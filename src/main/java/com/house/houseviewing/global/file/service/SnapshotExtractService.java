package com.house.houseviewing.global.file.service;

import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.global.file.dto.SnapshotUploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SnapshotExtractService {

    private final SnapshotUploadService snapshotUploadService;

    public RegistrySnapshotEntity register(MultipartFile snapshot) {
        SnapshotUploadResult response = snapshotUploadService.upload(snapshot);

        return RegistrySnapshotEntity.builder()
                .snapshotName(response.getSnapshotName())
                .snapshotUrl(response.getSnapshotUrl())
                .snapshotSizeBytes(response.getSnapshotSizeBytes())
                .build();
    }
}
