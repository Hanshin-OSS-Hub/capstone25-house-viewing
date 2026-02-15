package com.house.houseviewing.global.external.kakao.client;


import com.house.houseviewing.global.external.kakao.dto.KakaoAddressRQ;

public interface KakaoApiClient {

    KakaoAddressRQ result(String query);
}
