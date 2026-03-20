package com.house.houseviewing.domain.registrysnapshot.service;

import com.house.houseviewing.domain.common.DiagnosisType;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.repository.HouseRepository;
import com.house.houseviewing.domain.registrysnapshot.dto.request.PreContractDiagnosisRequest;
import com.house.houseviewing.domain.registrysnapshot.dto.response.SnapshotResultResponse;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshot.repository.RegistrySnapshotRepository;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.file.snapshot.service.SnapshotExtractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RegistrySnapshotService {

    private final HouseRepository houseRepository;
    private final RegistrySnapshotRepository registrySnapshotRepository;
    private final SnapshotExtractService snapshotExtractService;

    @Transactional
    public RegistrySnapshotEntity postRegister(Long houseId, MultipartFile snapshot){
        HouseEntity house = houseRepository.findById(houseId)
                .orElseThrow(() -> new AppException(ExceptionCode.HOUSE_NOT_FOUND));

        RegistrySnapshotEntity registrySnapshot = snapshotExtractService.register(snapshot);
        registrySnapshot.updateDiagnosisType(DiagnosisType.POSTCONTRACT);
        house.addRegistrySnapshot(registrySnapshot);

        return registrySnapshotRepository.save(registrySnapshot);
    }

    public List<SnapshotResultResponse> getSnapshots(Long userId){
        List<RegistrySnapshotEntity> snapshots = registrySnapshotRepository.findByHouseUserId(userId);

        return snapshots.stream()
                .map(SnapshotResultResponse::from)
                .toList();
    }
}
