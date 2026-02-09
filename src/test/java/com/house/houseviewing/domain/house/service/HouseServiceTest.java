package com.house.houseviewing.domain.house.service;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.repository.HouseRepository;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.domain.user.repository.UserRepository;
import com.house.houseviewing.domain.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class HouseServiceTest {

    @Autowired UserRepository userRepository;
    @Autowired UserService userService;

    @Autowired HouseService houseService;
    @Autowired HouseRepository houseRepository;

    @Test
    @DisplayName("엔티티 연관관계 확인")
    void 연관관계(){
        UserRegisterRQ request = new UserRegisterRQ("유인근", "yooyoo9191@gmail.com", "yooyoo9191", "okok0635!");
        Long id = userService.register(request);
        UserEntity user = userRepository.findById(id).get();

        Address address = new Address("경기도 오산시", "양산동 387", "18123");
        HouseEntity house1 = new HouseEntity("우리집1", address);
        HouseEntity house2 = new HouseEntity("우리집2", address);

        user.addHouse(house1);
        user.addHouse(house2);

        houseRepository.save(house1);
        houseRepository.save(house2);

        List<HouseEntity> houses = user.getHouses();
        for (HouseEntity house : houses) {
            System.out.println("house = " + house.getNickname());
        }
    }

    private static HouseEntity registerHouse() {
        Address address = new Address("경기도 오산시", "양산동 387", "18123");
        return new HouseEntity("우리집", address);
    }

    private UserEntity user() {
        UserRegisterRQ request = new UserRegisterRQ("유인근", "yooyoo9191@gmail.com", "yooyoo9191", "okok0635!");
        Long id = userService.register(request);
        UserEntity user = userRepository.findById(id).get();
        return user;
    }

}