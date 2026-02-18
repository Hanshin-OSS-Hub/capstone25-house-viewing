package com.house.houseviewing.domain.house.model.register;

import com.house.houseviewing.domain.common.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HouseRegisterRS {

    private Long houseId;

    private Address address;

}
