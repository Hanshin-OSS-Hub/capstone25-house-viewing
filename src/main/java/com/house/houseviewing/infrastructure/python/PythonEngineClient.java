package com.house.houseviewing.infrastructure.python;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class PythonEngineClient {

    @Qualifier("pythonWebClient")
    private final WebClient pythonWebClient;

    public void sendPdf(MultipartFile file){

    }


}
