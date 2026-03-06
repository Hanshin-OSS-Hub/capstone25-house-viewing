package com.house.houseviewing.domain.user;

import com.house.houseviewing.domain.user.model.findid.UserFindIdRQ;
import com.house.houseviewing.global.security.model.UserLoginRQ;
import com.house.houseviewing.domain.user.model.password.reset.UserResetPasswordRQ;
import com.house.houseviewing.domain.user.model.password.verify.UserVerifyPasswordRQ;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.fixture.UserFixture;
import com.house.houseviewing.domain.user.service.UserService;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.repository.UserRepository;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
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
class UserServiceTest {

    @InjectMocks UserService userService;
    @Mock UserRepository userRepository;

    @Nested
    @DisplayName("회원가입")
    class Register{

        @Test
        @DisplayName("성공")
        void 성공(){
            // given
            UserEntity build = UserFixture.createDefault().build();
            UserRegisterRQ build1 = UserFixture.createRegister(build).build();
            given(userRepository.existsByEmail(anyString()))
                    .willReturn(false);
            given(userRepository.existsByLoginId(anyString()))
                    .willReturn(false);
            given(userRepository.save(any(UserEntity.class)))
                    .willReturn(build);
            // when
            UserEntity register = userService.register(build1);
            // then
            assertThat(register).isNotNull();
            assertThat(build1.getLoginId()).isEqualTo(register.getLoginId());
        }

        @Test
        @DisplayName("실패: 아이디 중복")
        void 아이디_중복_예외_발생(){
            // given
            UserEntity build = UserFixture.createDefault().build();
            UserRegisterRQ request = UserFixture.createRegister(build).build();

            given(userRepository.existsByLoginId(anyString()))
                    .willReturn(true);
            // when
            // then
            assertThatThrownBy(() -> userService.register(request))
                    .isInstanceOf(AppException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.DUPLICATE_LOGIN_ID);
        }

        @Test
        @DisplayName("실패: 이메일 중복")
        void 이메일_중복_예외_발생(){
            // given
            UserEntity build = UserFixture.createDefault().build();
            UserRegisterRQ build1 = UserFixture.createRegister(build).build();

            given(userRepository.existsByEmail(anyString()))
                    .willReturn(true);
            // when
            // then
            assertThatThrownBy(() -> userService.register(build1))
                    .isInstanceOf(AppException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.DUPLICATE_EMAIL);
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login{
        @Test
        @DisplayName("성공")
        void 성공(){
            // given
            UserEntity user = UserFixture.createDefault().build();
            UserLoginRQ request = UserFixture.createLogin(user).build();
            given(userRepository.findByLoginIdAndPassword(anyString(), anyString()))
                    .willReturn(Optional.of(user));
            // when
            Long login = userService.login(request);
            // then
            assertThat(login).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("실퍠: 리소스가 일치하지 않음")
        void 로그인_실패_예외_발생(){
            // given
            UserEntity build = UserFixture.createDefault().build();
            UserLoginRQ build1 = UserFixture.createLogin(build).build();
            given(userRepository.findByLoginIdAndPassword(anyString(), anyString()))
                    .willReturn(Optional.empty());
            // when
            // then
            assertThatThrownBy(() -> userService.login(build1))
                    .isInstanceOf(AppException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.LOGIN_FAILED);
        }
    }

    @Nested
    @DisplayName("아이디 찾기")
    class FindLoginId{

        @Test
        @DisplayName("성공")
        void 성공(){
            // given
            UserEntity user = UserFixture.createDefault().build();
            UserFindIdRQ request = UserFixture.createFindId(user).build();
            given(userRepository.findByEmailAndName(anyString(), anyString()))
                    .willReturn(Optional.of(user));
            // when
            String id = userService.findId(request);
            // then
            assertThat(id).isEqualTo(user.getLoginId());
        }

        @Test
        @DisplayName("실패: 리소스가 일치하지 않음")
        void 아이디_찾기_실패_에외_발생(){
            // given
            UserEntity build = UserFixture.createDefault().build();
            UserFindIdRQ build1 = UserFixture.createFindId(build).build();
            given(userRepository.findByEmailAndName(anyString(), anyString()))
                    .willReturn(Optional.empty());
            // when
            // then
            assertThatThrownBy(() -> userService.findId(build1))
                    .isInstanceOf(AppException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.FIND_LOGIN_ID_FAILED);
        }
    }

    @Nested
    @DisplayName("비밀번호")
    class Password{

        @Test
        @DisplayName("권한 성공")
        void 권한_성공(){
            // given
            UserEntity user = UserFixture.createDefault().build();
            UserVerifyPasswordRQ request = UserFixture.createVerifyPassword(user).build();
            given(userRepository.findByEmailAndNameAndLoginId(anyString(), anyString() ,anyString()))
                    .willReturn(Optional.of(user));
            // when
            boolean b = userService.passwordVerify(request);
            // then
            assertThat(b).isTrue();
        }

        @Test
        @DisplayName("실패: 권한 리소스 일치하지 않음")
        void 비밀번호_권한_에외_발생(){
            // given
            UserEntity build = UserFixture.createDefault().build();
            UserVerifyPasswordRQ build1 = UserFixture.createVerifyPassword(build).build();
            given(userRepository.findByEmailAndNameAndLoginId(anyString(), anyString(), anyString()))
                    .willReturn(Optional.empty());
            // when
            // then
            assertThatThrownBy(() -> userService.passwordVerify(build1))
                    .isInstanceOf(AppException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.VERIFY_PASSWORD_FAILED);
        }

        @Test
        @DisplayName("비밀번호 변경 성공")
        void 변경_성공() {
            // given
            UserEntity user = UserFixture.createDefault()
                    .id(1L)
                    .build();

            UserResetPasswordRQ request = UserFixture.createResetPassword(user, "okok0635!", "okok0635!").build();
            given(userRepository.findById(1L))
                    .willReturn(Optional.of(user));
            // when
            userService.passwordReset(request);
            // then
            assertThat(user.getPassword()).isEqualTo("okok0635!");
        }

        @Test
        @DisplayName("실패: 새 비밀번호와 확인 비밀번호가 다름")
        void 비밀번호_변경_예외_발생(){
            // given
            UserEntity build = UserFixture.createDefault().build();
            UserResetPasswordRQ build1 = UserFixture.createResetPassword(build, "okok0635!", "okok0635").build();
            // when
            // then
            assertThatThrownBy(() -> userService.passwordReset(build1))
                    .isInstanceOf(AppException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.MISMATCH_PASSWORD);
        }
    }

    @Nested
    @DisplayName("유저 삭제")
    class Delete{

        @Test
        @DisplayName("성공")
        void 성공(){
            // given
            Long userId = 1L;
            UserEntity user = UserFixture.createDefault().build();
            given(userRepository.findById(userId))
                    .willReturn(Optional.of(user));
            // when
            userService.delete(userId);
            // then
            then(userRepository).should(times(1)).deleteById(userId);
        }

        @Test
        @DisplayName("실퍠: 해당 유저가 없음")
        void 유저_삭제_예외_발생(){
            // given
            UserEntity build = UserFixture.createDefault().build();
            given(userRepository.findById(build.getId()))
                    .willReturn(Optional.empty());
            // when
            // then
            assertThatThrownBy(() -> userService.delete(build.getId()))
                    .isInstanceOf(AppException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.USER_NOT_FOUND);
        }
    }

}

