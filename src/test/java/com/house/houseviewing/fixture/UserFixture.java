package com.house.houseviewing.fixture;

import com.house.houseviewing.domain.subscriptions.entity.SubscriptionEntity;
import com.house.houseviewing.domain.subscriptions.enums.PlanType;
import com.house.houseviewing.domain.users.entity.UserEntity;
import com.house.houseviewing.domain.users.dto.request.UserFindIdRequest;
import com.house.houseviewing.domain.common.auth.dto.UserLoginRequest;
import com.house.houseviewing.domain.users.dto.request.UserResetPasswordRequest;
import com.house.houseviewing.domain.users.dto.request.UserVerifyPasswordRequest;
import com.house.houseviewing.domain.users.dto.request.UserRegisterRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserFixture {

    public static UserEntity.UserEntityBuilder createDefault(){

        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .planType(PlanType.FREE)
                .build();

        return UserEntity.builder()
                .name("유인근")
                .loginId("yooyoo9191")
                .password("okok0635!")
                .email("yooyoo9191@gmail.com")
                .subscription(subscription);
    }

    public static UserRegisterRequest.UserRegisterRQBuilder createRegister(UserEntity user){
        return UserRegisterRequest.builder()
                .name(user.getName())
                .email(user.getEmail())
                .loginId(user.getLoginId())
                .password(user.getPassword());
    }

    public static UserLoginRequest.UserLoginRQBuilder createLogin(UserEntity user){
        return UserLoginRequest.builder()
                .loginId(user.getLoginId())
                .password(user.getPassword());
    }

    public static UserFindIdRequest.UserFindIdRQBuilder createFindId(UserEntity user){
        return UserFindIdRequest.builder()
                .name(user.getName())
                .email(user.getEmail());
    }

    public static UserVerifyPasswordRequest.UserVerifyPasswordRQBuilder createVerifyPassword(UserEntity user){
        return UserVerifyPasswordRequest.builder()
                .name(user.getName())
                .loginId(user.getLoginId())
                .email(user.getEmail());
    }

    public static UserResetPasswordRequest.UserResetPasswordRQBuilder createResetPassword(UserEntity user, String newPassword, String confirmPassword){
        return UserResetPasswordRequest.builder()
                .userId(user.getId())
                .newPassword(newPassword)
                .confirmPassword(confirmPassword);
    }
}
