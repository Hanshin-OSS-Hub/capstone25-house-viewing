package com.house.houseviewing.global.exception;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorRS> handleDuplicateLoginId(AppException e){
        ExceptionCode code = e.getExceptionCode();
        ErrorRS errorRS = new ErrorRS(code.getCode(), code.getMessage());
        log.info("status = "+code.getStatus());
        log.info("code  = "+code.getCode());
        log.info("message = "+code.getMessage());
        return ResponseEntity.status(code.getStatus()).body(errorRS);
    }


}
