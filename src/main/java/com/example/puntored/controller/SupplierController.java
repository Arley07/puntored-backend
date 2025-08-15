package com.example.puntored.controller;

import com.example.puntored.dto.response.SupplierResponse;
import com.example.puntored.service.SupplierService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public Flux<SupplierResponse> getSuppliers() {
        return supplierService.getSuppliers();
    }
}
