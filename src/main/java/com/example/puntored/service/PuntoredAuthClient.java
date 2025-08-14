package com.example.puntored.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PuntoredAuthClient {

    private static final Logger log = LoggerFactory.getLogger(PuntoredAuthClient.class);

    private final WebClient webClient;
    private final String apiKey;
    private final String user;
    private final String password;

    // Token en memoria y fecha de expiración
    private volatile String cachedToken;
    private volatile Instant expiry;
    private final ReentrantLock lock = new ReentrantLock();

    // Tiempo seguro de vida del token (en segundos)
    private static final long TOKEN_SAFE_TTL_SECONDS = 3500;

    public PuntoredAuthClient(WebClient.Builder webClientBuilder,
                              @Value("${puntored.api.url}") String baseUrl,
                              @Value("${puntored.api.key}") String apiKey,
                              @Value("${puntored.auth.user}") String user,
                              @Value("${puntored.auth.password}") String password) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.user = user;
        this.password = password;
    }

    /**
     * Devuelve un token válido de Puntored.
     * Si el token en caché sigue siendo válido, lo devuelve.
     * Si no, llama a /auth para obtener uno nuevo.
     */
    public String getToken() {
        if (cachedToken != null && expiry != null && Instant.now().isBefore(expiry)) {
            log.info("🔄 Usando token de Puntored desde caché");
            return cachedToken;
        }

        lock.lock();
        try {
            // Verificar otra vez dentro del lock para evitar carreras
            if (cachedToken != null && expiry != null && Instant.now().isBefore(expiry)) {
                log.info("🔄 Usando token de Puntored desde caché (dentro de lock)");
                return cachedToken;
            }

            log.info("📡 Solicitando nuevo token de Puntored...");
            Map<String, String> body = Map.of("user", user, "password", password);

            Map resp = webClient.post()
                    .uri("/auth")
                    .header("x-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (resp == null || !resp.containsKey("token")) {
                throw new RuntimeException("❌ No se obtuvo token de Puntored");
            }

            cachedToken = resp.get("token").toString(); // viene con "Bearer ..."
            expiry = Instant.now().plusSeconds(TOKEN_SAFE_TTL_SECONDS);

            log.info("✅ Nuevo token de Puntored obtenido y cacheado hasta {}", expiry);
            return cachedToken;

        } finally {
            lock.unlock();
        }
    }
}
