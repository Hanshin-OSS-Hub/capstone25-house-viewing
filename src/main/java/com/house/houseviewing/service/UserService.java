package com.house.houseviewing.service;

import com.house.houseviewing.dto.UserLoginRequest;
import com.house.houseviewing.dto.UserRegisterRequest;
import com.house.houseviewing.domain.User;
import com.house.houseviewing.exception.DuplicateLoginIdException;
import com.house.houseviewing.exception.LoginFailedException;
import com.house.houseviewing.repository.UserRepository;
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
    public Long register(UserRegisterRequest request){

        if(userRepository.existsAllByLoginId(request.getLoginId())){
            throw new DuplicateLoginIdException();
        }
        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getLoginId(),
                request.getPassword());

        userRepository.save(user);
        return user.getId();
    }

    public Long login(UserLoginRequest request){
        Optional<User> user = userRepository.findByLoginIdAndPassword(request.getLoginId(), request.getPassword());
        if (user.isEmpty()){
            throw new LoginFailedException();
        }
        User successUser = user.get();
        return successUser.getId();
    }

}
