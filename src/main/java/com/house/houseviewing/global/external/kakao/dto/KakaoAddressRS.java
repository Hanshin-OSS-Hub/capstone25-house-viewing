package com.house.houseviewing.global.external.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAddressRS {

    private List<Document> documents;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Document{
        private ParsedAddress address;
    }

    @Getter
    public static class ParsedAddress{

        @JsonProperty("address_name")
        private String addressName; // 경기도 오산시 양산동 387, 105호

        @JsonProperty("region_1depth_name")
        private String region1DepthName; // 경기도

        @JsonProperty("region_2depth_name")
        private String region2DepthName; // 오산시

        @JsonProperty("region_3depth_name")
        private String region3DepthName; // 양산동

        @JsonProperty("main_address_no")
        private String mainAddressNo; // 387

        @JsonProperty("sub_address_no")
        private String subAddressNo; // 105호
    }

}
