package com.house.houseviewing.domain.houses;

import com.house.houseviewing.domain.houses.entity.HouseEntity;
import com.house.houseviewing.domain.houses.dto.request.HouseRegisterRequest;
import com.house.houseviewing.domain.houses.repository.HouseRepository;
import com.house.houseviewing.domain.houses.service.HouseService;
import com.house.houseviewing.domain.users.entity.UserEntity;
import com.house.houseviewing.domain.users.dto.request.UserRegisterRequest;
import com.house.houseviewing.domain.users.service.UserService;
import com.house.houseviewing.fixture.HouseFixture;
import com.house.houseviewing.fixture.UserFixture;
import com.house.houseviewing.global.external.kakao.service.KakaoAddress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

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
        HouseEntity house = getHousesEntity(user);
        // when
        user.addHouse(house);
        house.addUser(user);
        // then
        assertThat(user.getId()).isEqualTo(house.getUser().getId());
    }

    @Test
    @DisplayName("집 삭제")
    void 집_삭제(){
        // given
        UserEntity user = getUserEntity();
        HouseEntity house = getHousesEntity(user);
        // when
        houseService.delete(house.getId());
        // then
        assertThat(houseRepository.findById(house.getId())).isEmpty();
    }

    private HouseEntity getHousesEntity(UserEntity user) {
        HouseEntity house = HouseFixture.createDefault(user).build();
        HouseRegisterRequest request = HouseFixture.createRegister(house).build();
        HouseEntity register = houseService.register(request);
        return register;

    }

    private UserEntity getUserEntity() {
        UserEntity user = UserFixture.createDefault().build();
        UserRegisterRequest requestUser = UserFixture.createRegister(user).build();
        UserEntity register = userService.register(requestUser);
        return register;
    }
}
