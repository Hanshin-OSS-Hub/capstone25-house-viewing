package com.house.houseviewing.domain.registrysnapshots.service;

import com.house.houseviewing.domain.common.DiagnosisType;
import com.house.houseviewing.domain.houses.entity.HouseEntity;
import com.house.houseviewing.domain.houses.repository.HouseRepository;
import com.house.houseviewing.domain.registrysnapshots.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshots.repository.RegistrySnapshotRepository;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.file.snapshot.service.SnapshotExtractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
}
