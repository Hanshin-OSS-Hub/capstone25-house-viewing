package com.house.houseviewing.global.external.kakao.service;

import com.house.houseviewing.global.external.kakao.model.KakaoAddressRS;
import reactor.core.publisher.Mono;

public interface KakaoAddress {

    Mono<KakaoAddressRS> parsingAddress(String query);
}
