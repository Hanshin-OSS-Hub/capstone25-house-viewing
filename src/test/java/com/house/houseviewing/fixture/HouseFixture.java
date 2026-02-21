package com.house.houseviewing.fixture;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.user.entity.UserEntity;

public class HouseFixture {

    public static HouseEntity.HouseEntityBuilder createDefault(UserEntity user){
        return HouseEntity.builder()
                .userEntity(user)
                .nickname("자취방")
                .address(AddressFixture.createAddress().build());
    }
}
