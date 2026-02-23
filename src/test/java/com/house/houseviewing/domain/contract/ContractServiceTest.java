package com.house.houseviewing.domain.contract;

import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.contract.model.ContractRegisterRQ;
import com.house.houseviewing.domain.contract.repository.ContractRepository;
import com.house.houseviewing.domain.contract.service.ContractService;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.repository.HouseRepository;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.fixture.ContractFixture;
import com.house.houseviewing.fixture.HouseFixture;
import com.house.houseviewing.fixture.UserFixture;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class ContractServiceTest {

    @InjectMocks ContractService contractService;

    @Mock ContractRepository contractRepository;
    @Mock HouseRepository houseRepository;

    @Nested
    @DisplayName("계약 등록")
    class Register{

        @Test
        @DisplayName("성공")
        void 성공(){
            // given
            UserEntity user = UserFixture.createDefault().build();
            HouseEntity house = HouseFixture.createDefault(user).id(1L).build();
            ContractEntity contract = ContractFixture.createDefault(house).build();
            ContractRegisterRQ request = ContractFixture.createRegister(contract).build();
            given(houseRepository.findById(1L))
                    .willReturn(Optional.of(house));
            given(contractRepository.save(any()))
                    .willReturn(contract);
            // when
            ContractEntity register = contractService.register(request);
            // then
            assertThat(register).isNotNull();
        }

        @Test
        @DisplayName("실패: 집을 찾을 수 없음")
        void 실패(){
            // given
            UserEntity user = UserFixture.createDefault().build();
            HouseEntity house = HouseFixture.createDefault(user).id(1L).build();
            ContractEntity contract = ContractFixture.createDefault(house).build();
            ContractRegisterRQ request = ContractFixture.createRegister(contract).build();
            given(houseRepository.findById(1L))
                    .willReturn(Optional.empty());
            // when
            // then
            assertThatThrownBy(() -> contractService.register(request))
                    .isInstanceOf(AppException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.HOUSE_NOT_FOUND);
        }
    }
}
