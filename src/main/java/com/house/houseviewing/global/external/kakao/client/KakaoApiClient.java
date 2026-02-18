package com.house.houseviewing.global.external.kakao.client;


import com.house.houseviewing.global.external.kakao.dto.KakaoAddressRS;

public interface KakaoApiClient {

    KakaoAddressRS searchAddress(String query);
}
