package com.house.houseviewing.infrastructure.python;

import com.house.houseviewing.global.file.pdf.dto.PdfReportRequest;
import com.house.houseviewing.global.file.snapshot.dto.SnapshotAnalysisResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PythonEngineClient {

    private final WebClient pythonWebClient;

    public PythonEngineClient(@Qualifier("pythonWebClient") WebClient pythonWebClient) {
        this.pythonWebClient = pythonWebClient;
    }

    public Mono<SnapshotAnalysisResult> sendPdf(MultipartFile file){
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource())
                .filename(file.getOriginalFilename());

        return pythonWebClient.post()
                .uri("/engine/analyze")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(SnapshotAnalysisResult.class);
    }

    public Mono<byte[]> sendDataAndReceivePdf(PdfReportRequest request){
        return pythonWebClient.post()
                .uri("/engine/generate-pdf")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(byte[].class);
    }
}
