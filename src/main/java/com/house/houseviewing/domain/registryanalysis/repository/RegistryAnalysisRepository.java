package com.house.houseviewing.domain.registryanalysis.repository;

import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistryAnalysisRepository extends JpaRepository<RegistrySnapshotEntity, Long> {
}
