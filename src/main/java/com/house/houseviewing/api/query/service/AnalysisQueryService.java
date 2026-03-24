package com.house.houseviewing.api.query.service;

import com.house.houseviewing.domain.analysis.postanalysis.dto.response.AnalysisResponse;
import com.house.houseviewing.domain.analysis.postanalysis.service.PostAnalysisService;
import com.house.houseviewing.domain.analysis.preanalysis.service.PreAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisQueryService {

    private final PostAnalysisService postAnalysisService;
    private final PreAnalysisService preAnalysisService;

    public List<AnalysisResponse> getAnalyses(Long userId){
        List<AnalysisResponse> postAnalyses = postAnalysisService.getPostAnalyses(userId);
        List<AnalysisResponse> preAnalyses = preAnalysisService.getPreAnalyses(userId);

        List<AnalysisResponse> result = new ArrayList<>();
        result.addAll(postAnalyses);
        result.addAll(preAnalyses);

        return result;
    }
}
