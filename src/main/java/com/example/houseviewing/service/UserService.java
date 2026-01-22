package com.example.houseviewing.service;

import com.example.houseviewing.domain.User;
import com.example.houseviewing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional
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

    @Transactional
    public String updatePassword(String password, String newPassword){
        User user = userRepository.findByPassword(password)
                .orElseThrow(() -> new IllegalArgumentException("비밀번호를 다시 입력해주세요"));
        user.changePassword(newPassword);
        return user.getPassword();
    }

    @Transactional
    public void deleteUser(String password){
        User user = userRepository.findByPassword(password)
                .orElseThrow(() -> new IllegalArgumentException("비밀번호가 틀렸습니다."));
        userRepository.delete(user);
    }

}
