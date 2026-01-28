package com.house.houseviewing.service;

import com.house.houseviewing.api.dto.UserRegisterRequest;
import com.house.houseviewing.domain.User;
import com.house.houseviewing.exception.DuplicateLoginIdException;
import com.house.houseviewing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long save(UserRegisterRequest request){

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

}
