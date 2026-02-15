package com.house.houseviewing.domain.common;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String fullAddress; // 경기도 오산시 양산동 387, 105호

    private String sido; // 경기도, 서울시

    private String sigungu; // 오산시, 송파구

    private String bname; // 양산동, 송파동

    private String jibun; // 387, 173-6

    private String detailAddress; // 105호

}