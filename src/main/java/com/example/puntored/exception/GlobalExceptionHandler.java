package com.example.puntored.exception;

import com.example.puntored.model.Transaction;
import com.example.puntored.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final TransactionRepository transactionRepository;

    public GlobalExceptionHandler(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, Object>> handleWebClientError(WebClientResponseException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now(ZoneId.of("America/Bogota")));
        body.put("status", ex.getStatusCode().value());
        body.put("error", "Error en API Puntored");
        body.put("message", ex.getResponseBodyAsString());

        try {
            Transaction tx = new Transaction();
            tx.setStatus("FAILED");
            tx.setTransactionMessage(ex.getResponseBodyAsString());
            transactionRepository.save(tx);
        } catch (Exception ignored) {
        }

        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }
}
