package com.house.houseviewing.domain.auth.service;

import com.house.houseviewing.domain.auth.dto.request.UserLoginRequest;
import com.house.houseviewing.domain.auth.dto.response.UserLoginResponse;
import com.house.houseviewing.domain.auth.jwt.JwtTokenProvider;
import com.house.houseviewing.domain.auth.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public UserLoginResponse login(UserLoginRequest request){
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLoginId(),
                        request.getPassword()
                ));
        CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();
        String accessToken = jwtTokenProvider.createToken(userDetails.getUserId(), userDetails.getUsername());
        String refreshToken = jwtTokenProvider.refreshToken(userDetails.getUserId(), userDetails.getUsername());
        
        return UserLoginResponse.from(accessToken, refreshToken);
    }
}
