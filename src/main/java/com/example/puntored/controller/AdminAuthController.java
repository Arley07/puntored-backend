package com.example.puntored.controller;

import com.example.puntored.model.User;
import com.example.puntored.repository.UserRepository;
import com.example.puntored.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AdminAuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminAuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
        }
        User user = userOpt.get();
        if (!user.isActive() || !passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoles());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "expiresIn", Long.valueOf(System.currentTimeMillis() + jwtUtil.getClaims(token).getExpiration().getTime()),
                "user", Map.of("id", user.getId(), "username", user.getUsername(), "roles", user.getRoles())
        ));
    }
}
