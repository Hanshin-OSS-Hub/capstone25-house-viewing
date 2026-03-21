package com.house.houseviewing.domain.analysis.postanalysis.repository;

import com.house.houseviewing.domain.analysis.postanalysis.entity.PostAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostAnalysisRepository extends JpaRepository<PostAnalysisEntity, Long> {

    Optional<PostAnalysisEntity> findTopBySnapshotIdOrderByCreatedAtDesc(Long snapshotId);

    @Query("""
        SELECT ra
        FROM PostAnalysisEntity ra
        JOIN ra.contract c
        JOIN c.house h
        JOIN h.user u
        WHERE u.id = :userId
        ORDER BY ra.createdAt DESC
    """)
    List<PostAnalysisEntity> findAllByUserId(Long userId);
}
