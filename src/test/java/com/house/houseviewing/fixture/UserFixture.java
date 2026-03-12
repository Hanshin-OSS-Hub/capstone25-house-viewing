package com.house.houseviewing.fixture;

import com.house.houseviewing.domain.subscription.entity.SubscriptionEntity;
import com.house.houseviewing.domain.subscription.enums.PlanType;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.dto.request.UserFindIdRequest;
import com.house.houseviewing.domain.common.auth.dto.UserLoginRQ;
import com.house.houseviewing.domain.user.dto.request.UserResetPasswordRequest;
import com.house.houseviewing.domain.user.dto.request.UserVerifyPasswordRequest;
import com.house.houseviewing.domain.user.dto.request.UserRegisterRequest;
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

    public static UserLoginRQ.UserLoginRQBuilder createLogin(UserEntity user){
        return UserLoginRQ.builder()
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
