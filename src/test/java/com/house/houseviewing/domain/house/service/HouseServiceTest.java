package com.house.houseviewing.domain.house.service;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRQ;
import com.house.houseviewing.domain.house.repository.HouseRepository;
import com.house.houseviewing.domain.house.util.JsonUtil;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.domain.user.repository.UserRepository;
import com.house.houseviewing.domain.user.service.UserService;
import com.house.houseviewing.global.external.kakao.service.KakaoAddress;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean private KakaoAddress kakaoAddress;

    @Test
    @DisplayName("집 등록")
    void 집_등록(){

        UserEntity user = userRegister();
        HouseEntity house = houseRegister(user.getId());

        assertThat(user.getId()).isEqualTo(house.getId());
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
        HouseRegisterRQ registerRQ = new HouseRegisterRQ(userid, "자취방", "서울 강남구 역삼동 830-31, 105호");
        HouseEntity house = houseService.register(registerRQ);
        return house;
    }

}