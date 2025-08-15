package com.example.puntored.exception;

import com.example.puntored.model.Transaction;
import com.example.puntored.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    private final TransactionRepository transactionRepository;

    public ValidationExceptionHandler(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getDefaultMessage())
                .collect(Collectors.toList());

        Object supplierVal = ex.getBindingResult().getFieldValue("supplierId");
        Object cellVal = ex.getBindingResult().getFieldValue("cellPhone");
        Object valueVal = ex.getBindingResult().getFieldValue("value");

        String supplierId = supplierVal != null ? supplierVal.toString() : "UNKNOWN";
        String cellPhone = cellVal != null ? cellVal.toString() : "0000000000";
        Double value = 0.0;
        if (valueVal != null) {
            try {
                value = Double.valueOf(valueVal.toString());
            } catch (NumberFormatException ignored) {}
        }

        try {
            Transaction tx = new Transaction();
            tx.setSupplierId(supplierId);
            tx.setCellPhone(cellPhone);
            tx.setValue(value);
            tx.setStatus("FAILED");
            tx.setTransactionMessage(String.join("; ", errores));
            transactionRepository.save(tx);
            log.warn("Transacción inválida guardada con valores por defecto");
        } catch (Exception e) {
            log.error("Error guardando transacción inválida", e);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now(ZoneId.of("America/Bogota")));
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validación fallida");
        response.put("messages", errores);

        return ResponseEntity.badRequest().body(response);
    }
}
