package com.house.houseviewing.infrastructure.python;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class PythonEngineClient {

    private final WebClient pythonWebClient;

}
