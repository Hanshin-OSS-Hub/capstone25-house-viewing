package com.house.houseviewing.global.security.auth;

import com.house.houseviewing.global.security.model.UserLoginRQ;
import com.house.houseviewing.global.security.model.UserLoginRS;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    public UserLoginRS login(UserLoginRQ request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLoginId(),
                        request.getPassword()
                )
        );
        return new UserLoginRS("LOGIN_SUCCESS");
    }
}
