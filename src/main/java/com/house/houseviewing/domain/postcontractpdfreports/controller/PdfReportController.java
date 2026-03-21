package com.house.houseviewing.domain.postcontractpdfreports.controller;

import com.house.houseviewing.domain.postcontractpdfreports.service.PdfReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pdf")
@RequiredArgsConstructor
public class PdfReportController {

    private final PdfReportService pdfReportService;
}
