package com.example.puntored.service;

import com.example.puntored.dto.request.AuthRequest;
import com.example.puntored.dto.response.AuthResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;

@Service
public class PuntoredAuthService {
    private final WebClient webClient;
    //private final String authUrl;
    private final String apiKey;
    private final String user;
    private final String password;

    public PuntoredAuthService(
            WebClient.Builder webClientBuilder,
            @Value("${puntored.api.url}") String apiUrl,
            @Value("${puntored.api.key}") String apiKey,
            @Value("${puntored.auth.user}") String user,
            @Value("${puntored.auth.password}") String password) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        //this.authUrl = apiUrl + "/auth";
        this.apiKey = apiKey;
        this.user = user;
        this.password = password;
    }

    public Mono<String> authenticate() {
        AuthRequest request = new AuthRequest();
        request.setUser(user);
        request.setPassword(password);

        return webClient.post()
                .uri("/auth")
                .header("x-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AuthResponse.class)
                .map(AuthResponse::getToken);
    }
}