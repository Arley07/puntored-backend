package com.example.puntored.service;

import com.example.puntored.dto.request.BuyRequest;
import com.example.puntored.dto.response.BuyResponse;
import com.example.puntored.model.Transaction;
import com.example.puntored.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.regex.Pattern;

@Service
public class BuyService {

    private static final Logger log = LoggerFactory.getLogger(BuyService.class);

    private final WebClient webClient;
    private final TransactionRepository transactionRepository;
    private final PuntoredAuthClient puntoredAuthClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey;
    private final Pattern cellPhonePattern = Pattern.compile("^3\\d{9}$");

    public BuyService(WebClient.Builder webClientBuilder,
                      @Value("${puntored.api.url}") String puntoredApiUrl,
                      TransactionRepository transactionRepository,
                      PuntoredAuthClient puntoredAuthClient,
                      @Value("${puntored.api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(puntoredApiUrl).build();
        this.transactionRepository = transactionRepository;
        this.puntoredAuthClient = puntoredAuthClient;
        this.apiKey = apiKey;
    }

    public BuyResponse processPurchase(BuyRequest request) {
        // Validaciones
        if (request == null) throw new IllegalArgumentException("Request vacío");
        if (request.getSupplierId() == null || request.getSupplierId().isBlank())
            throw new IllegalArgumentException("supplierId es obligatorio");
        if (request.getCellPhone() == null || !cellPhonePattern.matcher(request.getCellPhone()).matches())
            throw new IllegalArgumentException("cellPhone inválido. Debe empezar por 3 y tener 10 dígitos");
        if (request.getValue() == null || request.getValue() < 1000.0 || request.getValue() > 100000.0)
            throw new IllegalArgumentException("value inválido. Debe estar entre 1,000 y 100,000");

        // Crear transacción inicial
        Transaction tx = new Transaction();
        tx.setSupplierId(request.getSupplierId());
        tx.setCellPhone(request.getCellPhone());
        tx.setValue(request.getValue());
        tx.setStatus("PENDING");
        try {
            tx.setRequestPayload(objectMapper.writeValueAsString(request));
        } catch (Exception e) {
            tx.setRequestPayload("{}");
        }
        final Transaction savedTx = transactionRepository.save(tx);

        // Token de Puntored
        String tokenPuntored = puntoredAuthClient.getToken();
        log.info("Token Puntored: {}", tokenPuntored);
        log.info("Payload a Puntored: {}", tx.getRequestPayload());

        try {
            BuyResponse resp = webClient.post()
                    .uri("/buy")
                    .header("Authorization", tokenPuntored)
                    .header("x-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(BuyResponse.class)
                    .block();                                    // <<-- bloquea aquí

            if (resp == null) throw new IllegalStateException("Respuesta vacía de Puntored");

            savedTx.setStatus("SUCCESS");
            savedTx.setTransactionalId(resp.getTransactionalId());
            savedTx.setTransactionMessage(resp.getMessage());
            try {
                savedTx.setRawResponse(objectMapper.writeValueAsString(resp));
            } catch (Exception e) {
                savedTx.setRawResponse(resp.toString());
            }
            transactionRepository.save(savedTx);
            log.info("Transacción exitosa: {}", resp.getTransactionalId());
            return resp;

        } catch (RuntimeException e) { // incluye WebClientResponseException
            savedTx.setStatus("FAILED");
            savedTx.setTransactionMessage(e.getMessage());
            transactionRepository.save(savedTx);
            log.error("Error en la transacción: {}", e.getMessage());
            throw e;
        }
    }
}
