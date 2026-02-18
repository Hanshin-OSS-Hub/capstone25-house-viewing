package com.house.houseviewing.global.external.kakao.client;

import com.house.houseviewing.global.external.kakao.dto.KakaoAddressRS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KakaoApiClientImpl implements KakaoApiClient{

    private final WebClient webClient;

    public KakaoApiClientImpl(
            @Value("${kakao.api.key}") String kakaoApiKey,
            @Value("${kakao.api.url}") String kakaoApiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(kakaoApiUrl)
                .defaultHeader("Authorization", "KakaoAK " + kakaoApiKey)
                .build();
    }

    @Override
    public KakaoAddressRS searchAddress(String query) {
        return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/address.json")
                        .queryParam("query", query)
                            .build())
                        .retrieve()
                        .bodyToMono(KakaoAddressRS.class)
                        .block();
    }
}
