package com.house.houseviewing.domain.user.service;

import com.house.houseviewing.domain.global.exception.AppException;
import com.house.houseviewing.domain.global.exception.ExceptionCode;
import com.house.houseviewing.domain.user.model.findid.UserFindIdRQ;
import com.house.houseviewing.domain.user.model.login.UserLoginRQ;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.domain.global.jpa.entity.UserEntity;
import com.house.houseviewing.domain.global.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long register(UserRegisterRQ request){

        if(userRepository.existsAllByLoginId(request.getLoginId())){
            throw new AppException(ExceptionCode.DUPLICATE_LOGIN_ID);
        }
        UserEntity userEntity = new UserEntity(
                request.getName(),
                request.getEmail(),
                request.getLoginId(),
                request.getPassword());

        userRepository.save(userEntity);
        return userEntity.getId();
    }

    public Long login(UserLoginRQ request){
        Optional<UserEntity> user = userRepository.findByLoginIdAndPassword(request.getLoginId(), request.getPassword());
        if (user.isEmpty()){
            throw new AppException(ExceptionCode.LOGIN_FAILED);
        }
        UserEntity userEntity = user.get();
        return userEntity.getId();
    }

    public String findId(UserFindIdRQ request){
        Optional<UserEntity> user = userRepository.findByEmailAndName(request.getEmail(), request.getName());
        if(user.isEmpty()){
            throw new AppException(ExceptionCode.FIND_LOGIN_ID_FAILED);
        }
        UserEntity userEntity = user.get();
        return userEntity.getLoginId();
    }
}
