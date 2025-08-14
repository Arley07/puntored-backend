package com.example.puntored.controller;

import com.example.puntored.dto.request.BuyRequest;
import com.example.puntored.model.Transaction;
import com.example.puntored.repository.TransactionRepository;
import com.example.puntored.security.JwtUtil;
import com.example.puntored.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/transactions")
public class TransactionAdminController {

    private final TransactionService transactionService;
    private final JwtUtil jwtUtil;
    private final TransactionRepository transactionRepository;

    public TransactionAdminController(TransactionService transactionService, JwtUtil jwtUtil, TransactionRepository transactionRepository) {
        this.transactionService = transactionService;
        this.jwtUtil = jwtUtil;
        this.transactionRepository = transactionRepository;
    }

    // Helper: validar token y rol
    private ResponseEntity<?> checkAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token requerido"));
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }
        String roles = jwtUtil.getRoles(token);
        if (roles == null || !roles.contains("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Se requiere ROLE_ADMIN"));
        }
        return null; // ok
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam Optional<String> cellPhone,
            @RequestParam Optional<LocalDate> from,
            @RequestParam Optional<LocalDate> to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        ResponseEntity<?> check = checkAdmin(authHeader);
        if (check != null) return check;

        Page<Transaction> results = transactionService.search(cellPhone, from, to, PageRequest.of(page, size));
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                    @PathVariable String id) {
        ResponseEntity<?> check = checkAdmin(authHeader);
        if (check != null) return check;
        Transaction t = transactionRepository.findById(id).orElse(null);
        if (t == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "No encontrado"));
        return ResponseEntity.ok(t);
    }

    @PostMapping
    public ResponseEntity<?> createManual(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                          @RequestBody BuyRequest req) {
        ResponseEntity<?> check = checkAdmin(authHeader);
        if (check != null) return check;
        String token = authHeader.substring(7);
        String userId = jwtUtil.getUserId(token);
        // crear manualmente como PENDING (o podrías marcar SUCCESS si quieres)
        Transaction tx = transactionService.createPending(req, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(tx);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDelete(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                        @PathVariable String id) {
        ResponseEntity<?> check = checkAdmin(authHeader);
        if (check != null) return check;
        String token = authHeader.substring(7);
        String userId = jwtUtil.getUserId(token);
        Transaction tx = transactionService.softDelete(id, userId);
        return ResponseEntity.ok(Map.of("message", "transaction soft-deleted", "id", tx.getId()));
    }
}
