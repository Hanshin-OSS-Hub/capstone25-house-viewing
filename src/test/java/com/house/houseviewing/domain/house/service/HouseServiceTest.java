package com.house.houseviewing.domain.house.service;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRQ;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRS;
import com.house.houseviewing.domain.house.repository.HouseRepository;
import com.house.houseviewing.domain.house.util.JsonUtil;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class HouseServiceTest {

    @Autowired UserRepository userRepository;
    @Autowired UserService userService;

    @Autowired HouseService houseService;
    @Autowired HouseRepository houseRepository;

    @Test
    @DisplayName("집 등록")
    void 집_등록(){

        UserEntity user = userRegister();
        HouseEntity house = houseRegister(user.getId());

        assertThat(user).isEqualTo(house.getUserEntity());
    }

    @Test
    @DisplayName("집 삭제")
    void 집_삭제(){

        // given
        UserEntity user = userRegister();
        HouseEntity house = houseRegister(user.getId());

        // when
        houseService.delete(house.getId());

        // then
        Optional<HouseEntity> result = houseRepository.findById(house.getId());
        assertThat(result).isEmpty();
    }

    UserEntity userRegister() {
        String userJson = """
                {
                 "name": "유인근",
                 "email": "yooyoo9191@gmail.com",
                 "loginId": "yooyoo9191",
                 "password": "okok0635!"
                }
                """;
        UserRegisterRQ userRegisterRQ = JsonUtil.fromJson(userJson, UserRegisterRQ.class);
        Long register = userService.register(userRegisterRQ);
        UserEntity user = userRepository.findById(register).get();
        return user;
    }

    HouseEntity houseRegister(Long userid){
        String houseJson = """
                {
                     "userId" : %d,
                     "nickname" : "잠실 자취방",
                     "city" : "서울 송파구",
                     "street" : "송파동 173-6",
                     "zipcode" : "11111"
                }
                """.formatted(userid);
        HouseRegisterRQ registerRQ = JsonUtil.fromJson(houseJson, HouseRegisterRQ.class);
        HouseRegisterRS register = houseService.register(registerRQ);
        HouseEntity house = houseRepository.findById(register.getHouseId()).get();
        return house;
    }

}