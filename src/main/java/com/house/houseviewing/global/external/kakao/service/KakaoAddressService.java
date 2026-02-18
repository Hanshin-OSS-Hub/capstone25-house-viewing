package com.house.houseviewing.global.external.kakao.service;

import com.house.houseviewing.global.external.kakao.model.KakaoAddressRS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KakaoAddressService implements KakaoAddress {

    private final WebClient webClient;

    public KakaoAddressService(
            @Value("${kakao.api.url}") String kakaoUrl,
            @Value("${kakao.api.key}") String kakaoKey ) {
        this.webClient = WebClient.builder()
                .baseUrl(kakaoUrl)
                .defaultHeader("Authorization", "KakaoAK " + kakaoKey)
                .build();
    }

    @Override
    public Mono<KakaoAddressRS> parsingAddress(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/address.json")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .bodyToMono(KakaoAddressRS.class);
    }
}
