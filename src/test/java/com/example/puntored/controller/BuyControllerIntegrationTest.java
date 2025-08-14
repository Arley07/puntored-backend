package com.example.puntored.controller;

import com.example.puntored.dto.request.BuyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BuyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 🔹 Obtiene token de login antes de probar
    private String getAdminToken() throws Exception {
        String loginJson = """
                {
                    "username": "admin",
                    "password": "admin123*"
                }
                """;

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extrae solo el token usando Jackson
        return objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void buy_withValidToken_returnsSuccessResponse() throws Exception {
        String token = getAdminToken();

        BuyRequest request = new BuyRequest();
        request.setCellPhone("3123450024");
        request.setSupplierId("8753");
        request.setValue(15000.0);

        mockMvc.perform(post("/api/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Recarga exitosa"))
                .andExpect(jsonPath("$.cellPhone").value("3123450024"))
                .andExpect(jsonPath("$.value").value(15000.0))
                .andExpect(jsonPath("$.transactionalId").exists());
    }

    @Test
    void buy_withoutToken_returnsUnauthorized() throws Exception {
        BuyRequest request = new BuyRequest();
        request.setCellPhone("3123450024");
        request.setSupplierId("8753");
        request.setValue(15000.0);

        mockMvc.perform(post("/api/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
