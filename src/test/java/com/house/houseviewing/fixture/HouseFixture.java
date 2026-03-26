package com.house.houseviewing.fixture;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.dto.request.HouseRegisterRequest;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.enums.MonitoringStatus;

public class HouseFixture {

    public static HouseEntity.HouseEntityBuilder createDefault(UserEntity user){
        return HouseEntity.builder()
                .userEntity(user)
                .nickname("자취방")
                .monitoringStatus(MonitoringStatus.OFFLINE)
                .address(AddressFixture.createAddress().build());
    }

    public static HouseRegisterRequest.HouseRegisterRQBuilder createRegister(HouseEntity house){
        return HouseRegisterRequest.builder()
                .nickname(house.getNickname())
                .userId(house.getUser().getId())
                .originAddress("서울시 강남구");
    }
}
