package com.house.houseviewing.domain.analysis.preanalysis.repository;

import com.house.houseviewing.domain.analysis.preanalysis.entity.PreAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreAnalysisRepository extends JpaRepository<PreAnalysisEntity, Long> {
}
