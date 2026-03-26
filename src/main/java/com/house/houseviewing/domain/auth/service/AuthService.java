package com.house.houseviewing.domain.auth.service;

import com.house.houseviewing.domain.auth.dto.request.LoginRequest;
import com.house.houseviewing.domain.auth.dto.response.LoginResponse;
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

    public LoginResponse login(LoginRequest request){
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLoginId(),
                        request.getPassword()
                ));
        CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();
        String accessToken = jwtTokenProvider.createAccessToken(userDetails.getUserId(), userDetails.getUsername());
        String refreshToken = jwtTokenProvider.createRefreshToken(userDetails.getUserId(), userDetails.getUsername());
        refreshTokenService.saveRefreshToken(userDetails.getUserId(), refreshToken, jwtTokenProvider.getRefreshTokenExpiration());

        return LoginResponse.from(accessToken, refreshToken);
    }
}
