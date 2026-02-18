package com.house.houseviewing.global.external.kakao.service;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.global.external.kakao.client.KakaoApiClientImpl;
import com.house.houseviewing.global.external.kakao.dto.KakaoAddressRS;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoAddressService {

    private final KakaoApiClientImpl kakaoApiClient;

    public Address parsingAddress(String originAddress){

        KakaoAddressRS response = kakaoApiClient.searchAddress(originAddress);

        KakaoAddressRS.ParsedAddress documents = response.getDocuments()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("주소 검색 결과 없음"))
                .getAddress();

        Address address = new Address(documents.getAddressName());

        return address;
    }


}
