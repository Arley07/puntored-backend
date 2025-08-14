package com.example.puntored.dto.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String user;
    private String password;
}