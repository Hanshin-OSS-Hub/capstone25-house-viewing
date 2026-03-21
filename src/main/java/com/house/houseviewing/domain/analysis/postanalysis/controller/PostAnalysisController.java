package com.house.houseviewing.domain.analysis.postanalysis.controller;

import com.house.houseviewing.domain.analysis.postanalysis.entity.PostAnalysisEntity;
import com.house.houseviewing.domain.analysis.postanalysis.service.PostAnalysisService;
import com.house.houseviewing.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostAnalysisController {

    private final PostAnalysisService postAnalysisService;

    @GetMapping("/analyses")
    public ResponseEntity<List<PostAnalysisEntity>> getAnalyses(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<PostAnalysisEntity> result = postAnalysisService.getAnalyses(userDetails.getUserId());

        return ResponseEntity.ok(result);
    }

}
