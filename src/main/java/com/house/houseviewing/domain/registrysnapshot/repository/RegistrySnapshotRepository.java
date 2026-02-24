package com.house.houseviewing.domain.registrysnapshot.repository;

import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrySnapshotRepository extends JpaRepository<RegistrySnapshotEntity, Long> {
}
