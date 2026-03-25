package com.house.houseviewing.domain.report.postreport.repository;

import com.house.houseviewing.domain.analysis.postanalysis.entity.PostAnalysisEntity;
import com.house.houseviewing.domain.report.postreport.entity.PostReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReportRepository extends JpaRepository<PostReportEntity, Long> {

    Optional<PostAnalysisEntity> findTopByIdLessThanOrderByIdDesc(Long id);
}
