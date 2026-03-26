package com.house.houseviewing.domain.registrysnapshot.repository;

import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrySnapshotRepository extends JpaRepository<RegistrySnapshotEntity, Long> {

    Optional<RegistrySnapshotEntity> findTopByHouse_IdOrderByCreatedAtDesc(Long houseId);
    List<RegistrySnapshotEntity> findByHouseUserId(Long userId);
    long countByHouse_Id(Long houseId);
}
