package com.house.houseviewing.domain.users.service;

import com.house.houseviewing.domain.subscriptions.entity.SubscriptionEntity;
import com.house.houseviewing.domain.subscriptions.enums.PlanType;
import com.house.houseviewing.domain.users.dto.response.UserFindIdResponse;
import com.house.houseviewing.domain.users.dto.response.UserMeResponse;
import com.house.houseviewing.domain.users.dto.response.UserRegisterResponse;
import com.house.houseviewing.global.security.CustomUserDetails;
import com.house.houseviewing.domain.common.auth.dto.UserLoginResponse;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.domain.users.dto.request.UserFindIdRequest;
import com.house.houseviewing.domain.users.dto.request.UserResetPasswordRequest;
import com.house.houseviewing.domain.users.dto.request.UserVerifyPasswordRequest;
import com.house.houseviewing.domain.common.auth.dto.UserLoginRequest;
import com.house.houseviewing.domain.users.dto.request.UserRegisterRequest;
import com.house.houseviewing.domain.users.entity.UserEntity;
import com.house.houseviewing.domain.users.repository.UserRepository;
import com.house.houseviewing.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserRegisterResponse register(UserRegisterRequest request){
        duplicateUser(request);

        try{
            String password = passwordEncoder.encode(request.getPassword());
            UserEntity user = request.toEntity(password);
            SubscriptionEntity subscription = defaultSubscription();

            user.addSubscription(subscription);
            UserEntity savedUser = userRepository.save(user);

            return new UserRegisterResponse(savedUser.getId());
        } catch (DataIntegrityViolationException e){
            throw new AppException(ExceptionCode.DUPLICATE_RESOURCE);
        }
    }

    public UserLoginResponse login(UserLoginRequest request){
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLoginId(),
                        request.getPassword()
                ));
        CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();
        String token = jwtTokenProvider.createToken(userDetails.getUserId(), userDetails.getUsername());

        return UserLoginResponse.from(token);
    }

    public UserMeResponse me(Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));

        return UserMeResponse.from(user);
    }

    public UserFindIdResponse findLoginId(UserFindIdRequest request){
        UserEntity user = userRepository.findByEmailAndName(request.getEmail(), request.getName())
                .orElseThrow(() -> new AppException(ExceptionCode.FIND_LOGIN_ID_FAILED));

        return UserFindIdResponse.from(user);
    }

    public boolean passwordVerify(UserVerifyPasswordRequest request){
        UserEntity user = userRepository.findByEmailAndNameAndLoginId(request.getEmail(), request.getName(), request.getLoginId())
                .orElseThrow(() -> new AppException(ExceptionCode.VERIFY_PASSWORD_FAILED));
        return true;
    }

    @Transactional
    public void passwordReset(Long userId, UserResetPasswordRequest request){
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));
        String encode = passwordEncoder.encode(request.getNewPassword());
        user.updatePassword(encode);
    }

    @Transactional
    public void delete(Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }

    private void duplicateUser(UserRegisterRequest request) {
        if(userRepository.existsByLoginId(request.getLoginId())){
            throw new AppException(ExceptionCode.DUPLICATE_LOGIN_ID);
        }
        if(userRepository.existsByEmail(request.getEmail())){
            throw new AppException(ExceptionCode.DUPLICATE_EMAIL);
        }
    }

    private SubscriptionEntity defaultSubscription() {
        return SubscriptionEntity.builder()
                .planType(PlanType.FREE)
                .build();
    }
}
