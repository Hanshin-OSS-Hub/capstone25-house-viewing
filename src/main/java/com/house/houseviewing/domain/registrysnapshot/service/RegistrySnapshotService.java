package com.house.houseviewing.domain.registrysnapshot.service;

import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshot.repository.RegistrySnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RegistrySnapshotService {

    private final RegistrySnapshotRepository registrySnapshotRepository;

    @Transactional


}
