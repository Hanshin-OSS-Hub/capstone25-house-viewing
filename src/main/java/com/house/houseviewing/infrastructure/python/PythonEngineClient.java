package com.house.houseviewing.infrastructure.python;

import com.house.houseviewing.global.file.pdf.dto.PdfPostReportRequest;
import com.house.houseviewing.global.file.pdf.dto.PdfPreReportRequest;
import com.house.houseviewing.global.file.snapshot.dto.SnapshotPostAnalysisResult;
import com.house.houseviewing.global.file.snapshot.dto.SnapshotPreAnalysisResult;
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

    public Mono<SnapshotPostAnalysisResult> sendPostPdf(MultipartFile file){
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource())
                .filename(file.getOriginalFilename());

        return pythonWebClient.post()
                .uri("/engine/analyze")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(SnapshotPostAnalysisResult.class);
    }

    public Mono<SnapshotPreAnalysisResult> sendPrePdf(MultipartFile file){
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource())
                .filename(file.getOriginalFilename());

        return pythonWebClient.post()
                .uri("/engine/analyze")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(SnapshotPreAnalysisResult.class);
    }

    public Mono<byte[]> postSendDataAndReceivePdf(PdfPostReportRequest request){
        return pythonWebClient.post()
                .uri("/engine/generate-pdf")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(byte[].class);
    }

    public Mono<byte[]> preSendDataAndReceivePdf(PdfPreReportRequest request){
        return pythonWebClient.post()
                .uri("/engine/generate-pdf")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(byte[].class);
    }
}
