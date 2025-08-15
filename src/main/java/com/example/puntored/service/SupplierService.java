package com.example.puntored.service;

import com.example.puntored.dto.response.SupplierResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class SupplierService {

    private final WebClient webClient;
    private final PuntoredAuthClient puntoredAuthClient;
    private final String apiKey;

    public SupplierService(WebClient.Builder webClientBuilder,
                           @Value("${puntored.api.url}") String puntoredApiUrl,
                           PuntoredAuthClient puntoredAuthClient,
                           @Value("${puntored.api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(puntoredApiUrl).build();
        this.puntoredAuthClient = puntoredAuthClient;
        this.apiKey = apiKey;
    }

    public Flux<SupplierResponse> getSuppliers() {
        String token = puntoredAuthClient.getToken();

        return webClient.get()
                .uri("/getSuppliers")
                .header("Authorization", token) 
                .header("x-api-key", apiKey)     
                .retrieve()
                .bodyToFlux(SupplierResponse.class);
    }
}
