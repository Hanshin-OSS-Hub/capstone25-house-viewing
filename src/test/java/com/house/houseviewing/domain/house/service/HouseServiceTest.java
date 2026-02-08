package com.house.houseviewing.domain.house.service;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HouseServiceTest {

    @Test
    @DisplayName("엔티티 연관관계 확인")
    void 연관관계(){

    }

    private static HouseEntity registerHouse() {
        Address address = new Address("경기도 오산시", "양산동 387", "18123");
        return new HouseEntity("우리집", address);
    }

}