package com.house.houseviewing.domain.pdfreport.service;

import com.house.houseviewing.domain.pdfreport.repository.PdfReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PdfReportService {

    private final PdfReportRepository pdfReportRepository;
}
