package com.example.houseviewing.service;

import com.example.houseviewing.domain.User;
import com.example.houseviewing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void saveUser(User user){
        userRepository.save(user);
    }

    public String findUserId(String email, String name){
        User user = userRepository.findByEmailAndName(email, name)
                .orElseThrow(() -> new IllegalArgumentException("해당 정보로 가입된 회원이 없습니다."));

        return user.getLoginId();
    }

    public String findUserPassword(String loginId, String email, String name){
        User user = userRepository.findByLoginIdAndEmailAndName(loginId, email, name)
                .orElseThrow(() -> new IllegalArgumentException("해당 정보로 가입된 회원이 없습니다."));
        return user.getPassword();
    }

}
