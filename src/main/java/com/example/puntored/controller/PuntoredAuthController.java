package com.example.puntored.controller;

import com.example.puntored.service.PuntoredAuthService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class PuntoredAuthController {

    private final PuntoredAuthService authService;

    public PuntoredAuthController(PuntoredAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/token")
    public Mono<Map<String, String>> getToken() {
        return authService.authenticate()
                .map(token -> Collections.singletonMap("token", token));
    }
}
