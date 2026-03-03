package com.house.houseviewing.fixture;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRQ;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.enums.MonitoringStatus;

import java.util.ArrayList;

public class HouseFixture {

    public static HouseEntity.HouseEntityBuilder createDefault(UserEntity user){
        return HouseEntity.builder()
                .userEntity(user)
                .nickname("자취방")
                .monitoringStatus(MonitoringStatus.OFFLINE)
                .address(AddressFixture.createAddress().build());
    }

    public static HouseRegisterRQ.HouseRegisterRQBuilder createRegister(HouseEntity house){
        return HouseRegisterRQ.builder()
                .nickname(house.getNickname())
                .userId(house.getUserEntity().getId())
                .originAddress("서울시 강남구");
    }
}
