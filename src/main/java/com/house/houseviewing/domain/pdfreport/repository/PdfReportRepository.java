package com.house.houseviewing.domain.pdfreport.repository;

import com.house.houseviewing.domain.pdfreport.entity.PdfReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PdfReportRepository extends JpaRepository<PdfReportEntity, Long> {
}
