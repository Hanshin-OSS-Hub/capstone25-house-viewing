package com.house.houseviewing.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @Qualifier("pythonWebClient")
    public WebClient pythonWebClient(
            @Value("${python.api.url}") String pythonUrl){
        return WebClient.builder()
                .baseUrl(pythonUrl)
                .build();
    }

    @Bean
    @Qualifier("kakaoWebClient")
    public WebClient kakaoWebClient(
            @Value("${kakao.api.key}") String kakaoKey,
            @Value("${kakao.api.url}") String kakaoUrl){
        return WebClient.builder()
                .baseUrl(kakaoUrl)
                .defaultHeader("Authorization", "KakaoAK " + kakaoKey )
                .build();
    }
}
