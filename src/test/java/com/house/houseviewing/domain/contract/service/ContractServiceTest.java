package com.house.houseviewing.domain.contract.service;

import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.contract.enums.ContractType;
import com.house.houseviewing.domain.contract.model.ContractRegisterRQ;
import com.house.houseviewing.domain.contract.repository.ContractRepository;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRQ;
import com.house.houseviewing.domain.house.repository.HouseRepository;
import com.house.houseviewing.domain.house.service.HouseService;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
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

    @Autowired ContractRepository contractRepository;
    @Autowired ContractService contractService;

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
        Assertions.assertThat(byId.getId()).isEqualTo(contract.getId());

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