package com.example.puntored.config;

import com.example.puntored.model.User;
import com.example.puntored.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Profile("dev") // <-- clave
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    public DataSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        userRepository.findByUsername(adminUsername).ifPresentOrElse(u -> {
            // existe: no hacer nada
        }, () -> {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPasswordHash(encoder.encode(adminPassword));
            admin.setRoles("ROLE_ADMIN");
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println("=> Admin seed creado: " + adminUsername);
        });
    }
}
