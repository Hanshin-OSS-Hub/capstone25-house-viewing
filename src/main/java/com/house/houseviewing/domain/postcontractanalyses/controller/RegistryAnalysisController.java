package com.house.houseviewing.domain.postcontractanalyses.controller;

import com.house.houseviewing.domain.postcontractanalyses.entity.RegistryAnalysisEntity;
import com.house.houseviewing.domain.postcontractanalyses.service.RegistryAnalysisService;
import com.house.houseviewing.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RegistryAnalysisController {

    private final RegistryAnalysisService registryAnalysisService;

    @GetMapping("/analyses")
    public ResponseEntity<List<RegistryAnalysisEntity>> getAnalyses(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<RegistryAnalysisEntity> result = registryAnalysisService.getAnalyses(userDetails.getUserId());

        return ResponseEntity.ok(result);
    }

}
