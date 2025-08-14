package com.example.puntored.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuyResponse {
    private String message;

    /**
     * Puntored a veces devuelve "transactionalID" (con D mayúscula)
     * y otras veces "transactionalId". Aceptamos ambos con @JsonAlias.
     */
    @JsonProperty("transactionalId")
    @JsonAlias({"transactionalID"})
    private String transactionalId;

    private String cellPhone;
    private double value;

    // Constructor para respuestas exitosas
    public BuyResponse(String message, String transactionalId, String cellPhone, double value) {
        this.message = message;
        this.transactionalId = transactionalId;
        this.cellPhone = cellPhone;
        this.value = value;
    }

    // Constructor para respuestas de error
    public BuyResponse(String errorMessage) {
        this.message = errorMessage;
        // Los demás campos quedan con valores por defecto (null o 0.0)
    }
}