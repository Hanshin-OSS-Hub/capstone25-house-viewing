package com.house.houseviewing.domain.postcontractanalyses.repository;

import com.house.houseviewing.domain.postcontractanalyses.entity.RegistryAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RegistryAnalysisRepository extends JpaRepository<RegistryAnalysisEntity, Long> {

    Optional<RegistryAnalysisEntity> findTopBySnapshotIdOrderByCreatedAtDesc(Long snapshotId);

    @Query("""
        SELECT ra
        FROM RegistryAnalysisEntity ra
        JOIN ra.contract c
        JOIN c.house h
        JOIN h.user u
        WHERE u.id = :userId
        ORDER BY ra.createdAt DESC
    """)
    List<RegistryAnalysisEntity> findAllByUserId(Long userId);
}
