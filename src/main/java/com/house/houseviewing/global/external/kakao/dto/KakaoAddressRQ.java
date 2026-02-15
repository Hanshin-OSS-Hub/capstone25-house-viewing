package com.house.houseviewing.global.external.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.house.houseviewing.domain.common.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAddressRQ {

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Document{
        private Address address;
    }

    public static class Address{

        private String address_name; // 경기도 오산시 양산동 387, 105호
        private String region_1depth_name; // 경기도
        private String region_2depth_name; // 오산시
        private String region_3depth_name; // 양산동
        private String main_address_no; // 387
        private String sub_address_no; // 105호
    }

}
