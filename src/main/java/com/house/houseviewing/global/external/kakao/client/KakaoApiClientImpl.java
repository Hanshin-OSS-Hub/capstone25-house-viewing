package com.house.houseviewing.global.external.kakao.client;

import com.house.houseviewing.global.external.kakao.dto.KakaoAddressRQ;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class KakaoApiClientImpl implements KakaoApiClient{

    private final WebClient webClient;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Value("${kakao.api.url}")
    private String kakaoApiUrl;

    public KakaoApiClientImpl() {

        this.webClient = WebClient.builder()
                .baseUrl(kakaoApiUrl)
                .defaultHeader("Authorization", "KakaoAK " + kakaoApiKey)
                .build();
    }


    @Override
    public KakaoAddressRQ result(String query) {
        return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/address.json")
                        .queryParam("query", query)
                        .build())
                    .retrieve()

                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    return Mono.error(new IllegalArgumentException("카카오 API 요청 오류: 주소가 옳지 않거 인증에 실패했습니다. (query = " + query + ")" ));
                     })
                     .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    return Mono.error(new RuntimeException("서버 장애 발생: 잠시 후 다시 시도해 주세요"));
                })
                    .bodyToMono(KakaoAddressRQ.class)
                    .block();
    }
}
