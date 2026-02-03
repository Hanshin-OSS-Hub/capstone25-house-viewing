package com.house.houseviewing.domain.user.service;

import com.house.houseviewing.domain.global.exception.AppException;
import com.house.houseviewing.domain.global.exception.ExceptionCode;
import com.house.houseviewing.domain.user.model.findid.UserFindIdRQ;
import com.house.houseviewing.domain.user.model.password.reset.UserResetPasswordRQ;
import com.house.houseviewing.domain.user.model.password.verify.UserVerifyPasswordRQ;
import com.house.houseviewing.domain.user.model.login.UserLoginRQ;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long register(UserRegisterRQ request){

        UserEntity userEntity = new UserEntity(
                request.getName(),
                request.getEmail(),
                request.getLoginId(),
                request.getPassword());

        try{
            userRepository.save(userEntity);
        } catch (DataIntegrityViolationException e){
            throw new AppException(ExceptionCode.DUPLICATE_LOGIN_ID);
        }

        return userEntity.getId();
    }

    public Long login(UserLoginRQ request){
        UserEntity user = userRepository.findByLoginIdAndPassword(request.getLoginId(), request.getPassword())
                .orElseThrow(() -> new AppException(ExceptionCode.LOGIN_FAILED));
        return user.getId();
    }

    public String findId(UserFindIdRQ request){
        UserEntity user = userRepository.findByEmailAndName(request.getEmail(), request.getName())
                .orElseThrow(() -> new AppException(ExceptionCode.FIND_LOGIN_ID_FAILED));
        return user.getLoginId();
    }

    public void passwordVerify(UserVerifyPasswordRQ request){
        UserEntity user = userRepository.findByEmailAndNameAndLoginId(request.getEmail(), request.getName(), request.getLoginId())
                .orElseThrow(() -> new AppException(ExceptionCode.VERIFY_PASSWORD_FAILED));
    }

    @Transactional
    public void passwordReset(UserResetPasswordRQ request){
        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new AppException(ExceptionCode.MISMATCH_PASSWORD);
        }
        UserEntity user = userRepository.findById(request.getUserId()).get();

        user.updatePassword(request.getConfirmPassword());
    }
}
