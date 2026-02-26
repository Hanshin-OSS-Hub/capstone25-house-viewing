package com.house.houseviewing.api.transfer.service;

import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshot.repository.RegistrySnapshotRepository;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.infrastructure.python.PythonEngineClient;
import com.house.houseviewing.infrastructure.python.model.pdf.PythonPdfRQ;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PdfTransferService {

    private final PythonEngineClient pythonEngineClient;
    private final RegistrySnapshotRepository registrySnapshotRepository;

    public void transfer(Long registrySnapshotId){
        RegistrySnapshotEntity registrySnapshot = registrySnapshotRepository.findById(registrySnapshotId)
                .orElseThrow(() -> new AppException(ExceptionCode.FILE_SAVE_FAILED));
        String rawData = registrySnapshot.getRawData();
    }
}
