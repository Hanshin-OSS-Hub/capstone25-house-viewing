package com.house.houseviewing.global.external.kakao.service;

import com.house.houseviewing.domain.common.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class KakaoAddressServiceTest {

    @Autowired KakaoAddressService kakaoAddressService;

    @Test
    @DisplayName("집 주소 데이터 파싱")
    void 카카오_API(){
        Address address = kakaoAddressService.parsingAddress("경기도 오산시 양산동 387, 105호");
        System.out.println("address = " + address);
    }
}