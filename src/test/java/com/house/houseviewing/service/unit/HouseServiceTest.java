package com.house.houseviewing.service.unit;

import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.contract.enums.ContractType;
import com.house.houseviewing.domain.contract.model.ContractRegisterRQ;
import com.house.houseviewing.domain.contract.repository.ContractRepository;
import com.house.houseviewing.domain.contract.service.ContractService;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRQ;
import com.house.houseviewing.domain.house.repository.HouseRepository;
import com.house.houseviewing.domain.house.service.HouseService;
import com.house.houseviewing.util.JsonUtil;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.domain.user.repository.UserRepository;
import com.house.houseviewing.domain.user.service.UserService;
import com.house.houseviewing.global.external.kakao.service.KakaoAddress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class HouseServiceTest {

    @Autowired UserRepository userRepository;
    @Autowired UserService userService;

    @Autowired
    HouseService houseService;
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
        UserEntity register = userService.register(userRegisterRQ);
        UserEntity user = userRepository.findById(register.getId()).get();
        return user;
    }

    HouseEntity houseRegister(Long userid){
        HouseRegisterRQ registerRQ = new HouseRegisterRQ(userid, "자취방", "서울 강남구 역삼동 830-31, 105호");
        HouseEntity house = houseService.register(registerRQ);
        return house;
    }

    @SpringBootTest
    @Transactional
    static
    class ContractServiceTest {

        @Autowired
        UserRepository userRepository;
        @Autowired
        UserService userService;

        @Autowired HouseService houseService;
        @Autowired
        HouseRepository houseRepository;

        @MockitoBean
        private KakaoAddress kakaoAddress;

        @Autowired
        ContractRepository contractRepository;
        @Autowired
        ContractService contractService;

        @Test
        @DisplayName("계약_등록")
        void 계약_등록(){
            // given
            UserEntity user = userRegister();
            HouseEntity house = houseRegister(user);
            ContractEntity contract = contractRegister(house);

            // when
            Long id = contract.getId();
            ContractEntity byId = contractRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("계약을 찾을 수 없습니다."));

            // then
            assertThat(byId.getId()).isEqualTo(contract.getId());

        }

        UserEntity userRegister() {
            UserRegisterRQ user = new UserRegisterRQ("유인근", "yooyoo9191@gmail.com", "yooyoo9191", "okok0635!");
            UserEntity register = userService.register(user);
            return register;
        }

        HouseEntity houseRegister(UserEntity user){
            HouseRegisterRQ registerRQ = new HouseRegisterRQ(user.getId(), "자취방", "서울 강남구 역삼동 830-31, 105호");
            HouseEntity register = houseService.register(registerRQ);
            register.setUserEntity(user);
            user.addHouse(register);
            return register;
        }

        ContractEntity contractRegister(HouseEntity house) {
            String contractJson = """
                    {
                      "houseId": %d,
                      "contractType": "JEONSE",
                      "deposit": 300000000,
                      "monthlyAmount": 0,
                      "maintenanceFee": 150000,
                      "moveDate": "2024-03-01",
                      "confirmDate": "2024-03-01"
                    }             
                    """.formatted(house.getId());
            ContractRegisterRQ contractRegisterRQ = ContractRegisterRQ.builder()
                    .houseId(house.getId())
                    .contractType(ContractType.JEONSE)
                    .deposit(30000000L)
                    .monthlyAmount(0L)
                    .maintenanceFee(20L)
                    .moveDate(LocalDate.of(2026, 3, 1))
                    .confirmDate(LocalDate.of(2026, 3, 5))
                    .build();
            ContractEntity register = contractService.register(contractRegisterRQ);
            house.addContract(register);
            register.setHouseEntity(house);
            return register;
        }
    }
}