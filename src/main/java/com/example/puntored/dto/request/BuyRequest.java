package com.example.puntored.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BuyRequest {
    @NotBlank(message = "El ID del proveedor es obligatorio")
    private String supplierId;

    @Pattern(regexp = "^3\\d{9}$", message = "El teléfono debe empezar con 3 y tener 10 dígitos")
    private String cellPhone;

    @Min(value = 1000, message = "El valor mínimo es 1,000")
    @Max(value = 100000, message = "El valor máximo es 100,000")
    private Double value; 
}
