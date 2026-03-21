package com.house.houseviewing.domain.postcontractpdfreports.repository;

import com.house.houseviewing.domain.postcontractpdfreports.entity.PdfReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PdfReportRepository extends JpaRepository<PdfReportEntity, Long> {
}
