package com.house.houseviewing.domain.postreport.repository;

import com.house.houseviewing.domain.postreport.entity.PdfReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PdfReportRepository extends JpaRepository<PdfReportEntity, Long> {
}
