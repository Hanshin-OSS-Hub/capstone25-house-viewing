package com.house.houseviewing.domain.user.service;

import com.house.houseviewing.domain.subscription.entity.SubscriptionEntity;
import com.house.houseviewing.domain.subscription.enums.PlanType;
import com.house.houseviewing.domain.user.model.login.UserLoginRS;
import com.house.houseviewing.global.config.PasswordConfig;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.domain.user.model.findid.UserFindIdRQ;
import com.house.houseviewing.domain.user.model.password.reset.UserResetPasswordRQ;
import com.house.houseviewing.domain.user.model.password.verify.UserVerifyPasswordRQ;
import com.house.houseviewing.domain.user.model.login.UserLoginRQ;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.repository.UserRepository;
import com.house.houseviewing.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordConfig passwordConfig;

    @Transactional
    public UserEntity register(UserRegisterRQ request){

        if(userRepository.existsByLoginId(request.getLoginId())){
            throw new AppException(ExceptionCode.DUPLICATE_LOGIN_ID);
        }
        if(userRepository.existsByEmail(request.getEmail())){
            throw new AppException(ExceptionCode.DUPLICATE_EMAIL);
        }

        try{
            UserEntity userEntity = UserEntity.builder()
                    .name(request.getName())
                    .loginId(request.getLoginId())
                    .email(request.getEmail())
                    .password(passwordConfig.passwordEncoder(request.getPassword()))
                    .build();

            SubscriptionEntity subscription = SubscriptionEntity.builder()
                    .user(userEntity)
                    .planType(PlanType.FREE)
                    .build();

            userEntity.updateSubscription(subscription);

            UserEntity saved = userRepository.save(userEntity);

            return saved;
        } catch (DataIntegrityViolationException e){
            throw new AppException(ExceptionCode.DUPLICATE_LOGIN_ID);
        }
    }

    public UserLoginRS login(UserLoginRQ request){
        UserEntity user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new AppException(ExceptionCode.LOGIN_FAILED));
        if(!user.getPassword().equals(request.getPassword())){
            throw new AppException(ExceptionCode.LOGIN_FAILED);
        }

        String token = jwtTokenProvider.createToken(user.getLoginId());
        UserLoginRS response = UserLoginRS.builder()
                .token(token)
                .loginId(user.getLoginId())
                .userId(user.getId())
                .build();

        return response;
    }

    public String findLoginId(UserFindIdRQ request){
        UserEntity user = userRepository.findByEmailAndName(request.getEmail(), request.getName())
                .orElseThrow(() -> new AppException(ExceptionCode.FIND_LOGIN_ID_FAILED));
        return user.getLoginId();
    }

    public boolean passwordVerify(UserVerifyPasswordRQ request){
        UserEntity user = userRepository.findByEmailAndNameAndLoginId(request.getEmail(), request.getName(), request.getLoginId())
                .orElseThrow(() -> new AppException(ExceptionCode.VERIFY_PASSWORD_FAILED));
        return true;
    }

    @Transactional
    public boolean passwordReset(UserResetPasswordRQ request){
        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new AppException(ExceptionCode.MISMATCH_PASSWORD);
        }
        UserEntity user = userRepository.findById(request.getUserId()).get();

        user.updatePassword(request.getConfirmPassword());
        return true;
    }

    @Transactional
    public void delete(Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));

        userRepository.deleteById(userId);
    }
}
