package com.example.puntored.controller;

import com.example.puntored.dto.request.BuyRequest;
import com.example.puntored.dto.response.BuyResponse;
import com.example.puntored.service.BuyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestController
@RequestMapping("/api/buy")
public class BuyController {

    private final BuyService buyService;

    public BuyController(BuyService buyService) {
        this.buyService = buyService;
    }

    @PostMapping
    public ResponseEntity<?> buy(@Valid @RequestBody BuyRequest request) {
        try {
            BuyResponse resp = buyService.processPurchase(request);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new BuyResponse(e.getMessage()));
        } catch (WebClientResponseException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new BuyResponse("Error interno: " + e.getMessage()));
        }
    }
}
