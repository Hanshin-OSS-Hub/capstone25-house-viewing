package com.house.houseviewing.domain.registryanalysis.repository;

import com.house.houseviewing.domain.registryanalysis.entity.RegistryAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistryAnalysisRepository extends JpaRepository<RegistryAnalysisEntity, Long> {

    Optional<RegistryAnalysisEntity> findTopBySnapshotIdOrderByCreatedAtDesc(Long snapshotId);
}
