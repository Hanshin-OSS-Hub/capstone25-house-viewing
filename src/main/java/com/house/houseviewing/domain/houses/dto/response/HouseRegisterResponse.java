package com.house.houseviewing.domain.houses.dto.response;

import com.house.houseviewing.domain.common.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor @Builder
public class HouseRegisterResponse {

    private Long houseId;

    private Address address;

}

