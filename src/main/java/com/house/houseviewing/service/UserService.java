package com.house.houseviewing.service;

import com.house.houseviewing.domain.User;
import com.house.houseviewing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Long save(User user){
        userRepository.save(user);
        return user.getId();
    }


}
