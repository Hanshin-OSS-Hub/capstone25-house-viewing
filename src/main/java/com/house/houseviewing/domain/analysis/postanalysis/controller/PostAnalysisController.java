package com.house.houseviewing.domain.analysis.postanalysis.controller;

import com.house.houseviewing.global.file.diff.dto.DiffAnalysisResponse;
import com.house.houseviewing.global.file.pdf.dto.PdfDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class PostAnalysisController {

    @GetMapping("/")
    public ResponseEntity<PdfDownloadResponse> diffDiagnose(){

    }
}
