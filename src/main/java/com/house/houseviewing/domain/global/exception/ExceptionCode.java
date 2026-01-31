package com.house.houseviewing.domain.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter @AllArgsConstructor
public enum ExceptionCode {

    DUPLICATE_LOGIN_ID("DB001",HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
    LOGIN_FAILED("VP001",HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 틀렸습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}


/*
AU: 로그인, 권한 토큰 -> 보안 관련
VP: 사용자가 잘못 보낸 값 -> 입력값 오류
DB: 데이터 충돌, 무결성 깨짐
ER: 서버가 터짐
NF: 조회했는데 없음
 */