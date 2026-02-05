package com.house.houseviewing.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorRS {

    private final String code;
    private final String message;

}
