package com.house.houseviewing.infrastructure.python;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class PythonEngineClient {

    private final WebClient pythonWebClient;

    public void sendPdf(MultipartFile file){


    }


}
