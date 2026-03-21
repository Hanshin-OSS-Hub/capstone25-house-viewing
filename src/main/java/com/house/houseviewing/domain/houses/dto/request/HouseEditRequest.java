package com.house.houseviewing.domain.houses.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @NoArgsConstructor
public class HouseEditRequest {

    private String address;

    private String nickname;

}
