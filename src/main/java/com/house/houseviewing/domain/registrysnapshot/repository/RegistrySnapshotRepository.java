package com.house.houseviewing.domain.registrysnapshot.repository;

import com.house.houseviewing.domain.registrysnapshot.dto.response.SnapshotResultResponse;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrySnapshotRepository extends JpaRepository<RegistrySnapshotEntity, Long> {

    Optional<RegistrySnapshotEntity> findTopByHouseEntityIdOrderByCreatedAtDesc(Long houseId);
    List<RegistrySnapshotEntity> findByHouseEntityUserEntityId(Long userId);
}
