package com.house.houseviewing.infrastructure.python;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PythonEngineClient {

    private final WebClient pythonWebClient;

    public PythonEngineClient(WebClient pythonWebClient) {
        this.pythonWebClient = pythonWebClient;
    }
}
