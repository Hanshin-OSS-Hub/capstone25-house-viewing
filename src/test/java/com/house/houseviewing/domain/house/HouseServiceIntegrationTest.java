package com.house.houseviewing.domain.house;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRQ;
import com.house.houseviewing.domain.house.repository.HouseRepository;
import com.house.houseviewing.domain.house.service.HouseService;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.domain.user.service.UserService;
import com.house.houseviewing.fixture.HouseFixture;
import com.house.houseviewing.fixture.UserFixture;
import com.house.houseviewing.global.external.kakao.service.KakaoAddress;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class HouseServiceIntegrationTest {

    @Autowired UserService userService;
    @Autowired HouseRepository houseRepository;
    @Autowired HouseService houseService;

    @MockitoBean KakaoAddress kakaoAddress;

    @Test
    @DisplayName("집 등록")
    void 집_등록(){
        // given
        UserEntity user = getUserEntity();
        HouseEntity house = getHouseEntity(user);
        // when
        user.addHouse(house);
        house.setUserEntity(user);
        // then
        assertThat(user.getId()).isEqualTo(house.getUserEntity().getId());
    }

    @Test
    @DisplayName("집 삭제")
    void 집_삭제(){
        // given
        UserEntity user = getUserEntity();
        HouseEntity house = getHouseEntity(user);
        // when
        houseService.delete(house.getId());
        // then
        assertThat(houseRepository.findById(house.getId())).isEmpty();
    }g

    private HouseEntity getHouseEntity(UserEntity user) {
        HouseEntity house = HouseFixture.createDefault(user).build();
        HouseRegisterRQ request = HouseFixture.createRegister(house).build();
        HouseEntity register = houseService.register(request);
        return register;
    }

    private UserEntity getUserEntity() {
        UserEntity user = UserFixture.createDefault().build();
        UserRegisterRQ requestUser = UserFixture.createRegister(user).build();
        UserEntity register = userService.register(requestUser);
        return register;
    }
}
