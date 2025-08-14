package com.example.puntored.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AuthResponse {
    @JsonProperty("token")
    private String token;
}