package com.house.houseviewing.domain.contract;

import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.contract.model.ContractRegisterRQ;
import com.house.houseviewing.domain.contract.repository.ContractRepository;
import com.house.houseviewing.domain.contract.service.ContractService;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRQ;
import com.house.houseviewing.domain.house.service.HouseService;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.domain.user.service.UserService;
import com.house.houseviewing.fixture.ContractFixture;
import com.house.houseviewing.fixture.HouseFixture;
import com.house.houseviewing.fixture.UserFixture;
import com.house.houseviewing.global.external.kakao.service.KakaoAddress;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ContractServiceIntegrationTest {

    @Autowired ContractService contractService;
    @Autowired ContractRepository contractRepository;
    @Autowired HouseService houseService;
    @Autowired UserService userService;

    @MockitoBean KakaoAddress kakaoAddress;

    @Test
    @DisplayName("계약 등록")
    void 계약_등록(){
        // given
        UserEntity user = getUserEntity();
        HouseEntity house = getHouseEntity(user);
        // when
        ContractEntity contract = getContract(house);
        // then
        assertThat(contract.getHouseEntity().getId()).isEqualTo(house.getId());

    }

    @Test
    @DisplayName("계약 삭제")
    void 계약_삭제(){
        // given
        UserEntity user = getUserEntity();
        HouseEntity house = getHouseEntity(user);
        ContractEntity contract = getContract(house);
        // when
        contractService.delete(contract.getId());
        // then
        assertThat(contractRepository.findById(contract.getId())).isEmpty();
    }

    private ContractEntity getContract(HouseEntity house) {
        ContractEntity contract = ContractFixture.createDefault(house).build();
        ContractRegisterRQ request = ContractFixture.createRegister(contract)
                .houseId(house.getId()).build();
        ContractEntity register = contractService.register(request);
        return register;
    }

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
