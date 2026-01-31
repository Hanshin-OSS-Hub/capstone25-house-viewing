package com.house.houseviewing.domain.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorRS {

    private final String code;
    private final String message;

}
