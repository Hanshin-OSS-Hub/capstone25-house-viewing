package com.house.houseviewing.domain.house;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.dto.request.HouseRegisterRequest;
import com.house.houseviewing.domain.house.repository.HouseRepository;
import com.house.houseviewing.domain.house.service.HouseService;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.repository.UserRepository;
import com.house.houseviewing.fixture.HouseFixture;
import com.house.houseviewing.fixture.UserFixture;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.external.kakao.service.KakaoAddress;
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
class HouseServiceTest {

    @InjectMocks HouseService houseService;

    @Mock HouseRepository houseRepository;
    @Mock UserRepository userRepository;

    @Mock KakaoAddress kakaoAddress;

    @Nested
    @DisplayName("집 등록")
    class Register{

        @Test
        @DisplayName("성공")
        void 성공(){
            // given
            UserEntity user = UserFixture.createDefault()
                    .id(1L).build();
            HouseEntity house = HouseFixture.createDefault(user).build();
            HouseRegisterRequest request = HouseFixture.createRegister(house)
                    .userId(1L).build();
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(user));
            given(houseRepository.save(any()))
                    .willReturn(house);
            // when
            HouseEntity register = houseService.register(request);
            // then
            assertThat(register).isNotNull();
            assertThat(register.getNickname()).isEqualTo(request.getNickname());
        }

        @Test
        @DisplayName("실패: 사용자를 찾을 수 없음")
        void 실패1(){
            // given
            UserEntity user = UserFixture.createDefault()
                    .id(1L).build();
            HouseEntity house = HouseFixture.createDefault(user).build();
            HouseRegisterRequest request = HouseFixture.createRegister(house)
                    .userId(1L).build();
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.empty());
            // when
            // then
            assertThatThrownBy(() -> houseService.register(request))
                    .isInstanceOf(AppException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("집 삭제")
    class Delete{

        @Test
        @DisplayName("성공")
        void 성공(){
            // given
            UserEntity user = UserFixture.createDefault().build();
            HouseEntity house = HouseFixture.createDefault(user)
                    .id(1L).build();
            given(houseRepository.findById(anyLong()))
                    .willReturn(Optional.of(house));
            // when
            houseService.delete(house.getId());
            // then
            then(houseRepository).should(times(1)).findById(1L);
        }

        @Test
        @DisplayName("실패: 집을 찾을 수 없음")
        void 실패(){
            // given
            UserEntity user = UserFixture.createDefault().id(1L).build();
            HouseEntity house = HouseFixture.createDefault(user).build();
            given(houseRepository.findById(anyLong()))
                    .willReturn(Optional.empty());
            // when
            // then
            assertThatThrownBy(() -> houseService.delete(1L))
                    .isInstanceOf(AppException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.HOUSE_NOT_FOUND);
        }
    }
}