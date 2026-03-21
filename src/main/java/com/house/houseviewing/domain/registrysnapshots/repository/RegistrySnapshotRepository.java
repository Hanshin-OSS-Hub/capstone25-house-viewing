package com.house.houseviewing.domain.registrysnapshots.repository;

import com.house.houseviewing.domain.registrysnapshots.entity.RegistrySnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrySnapshotRepository extends JpaRepository<RegistrySnapshotEntity, Long> {

    Optional<RegistrySnapshotEntity> findTopByHouseIdOrderByCreatedAtDesc(Long houseId);
    List<RegistrySnapshotEntity> findByHouseUserId(Long userId);
}
