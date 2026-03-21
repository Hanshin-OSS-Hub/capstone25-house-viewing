package com.house.houseviewing.domain.users.dto.response;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.houses.entity.HouseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserHousesResponse {

    private Long id;
    private Address address;
    private Long deposit;

    public UserHousesResponse(HouseEntity house) {
        this.id = house.getId();
        this.address = house.getAddress();

        if (house.getContracts() != null && !house.getContracts().isEmpty()){
            this.deposit = house.getContracts().get(0).getDeposit();
        }
        else {
            this.deposit = 0L;
        }
    }
}
