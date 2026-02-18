package com.house.houseviewing.domain.house.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/kakao")
public class AddressService {

    @GetMapping("/search")
    public String search(@RequestParam String query){
        Mono<String> stringMono = WebClient.builder().baseUrl("https://dapi.kakao.com")
                .build().get()
                .uri(builder -> builder.path("/v2/local/search/address.json")
                        .queryParam("query", query).build())
                .header("Authorization", "KakaoAK " + "ed9dbc0f803f1b803114cd67b7825e58")
                .exchangeToMono(response -> {
                    return response.bodyToMono(String.class);
                });
        return stringMono.block();
    }
}
