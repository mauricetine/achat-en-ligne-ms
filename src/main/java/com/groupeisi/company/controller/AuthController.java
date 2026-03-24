package com.groupeisi.company.controller;

import com.groupeisi.company.config.security.JwtUtil;
import com.groupeisi.company.dto.UserAccountDto;
import com.groupeisi.company.entities.UserAccount;
import com.groupeisi.company.repository.UserAccountRepository;
import com.groupeisi.company.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentification", description = "Login et inscription")
public class AuthController {

    private final UserAccountRepository userAccountRepository;
    private final UserAccountService userAccountService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur")
    public ResponseEntity<UserAccountDto> register(@RequestBody UserAccountDto dto) {
        log.info("Inscription de l'utilisateur : {}", dto.getEmail());
        return ResponseEntity.ok(userAccountService.create(dto));
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion - retourne un token JWT")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Tentative de connexion : {}", request.getEmail());
        UserAccount user = userAccountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Mot de passe incorrect pour : {}", request.getEmail());
            return ResponseEntity.status(401).body(Map.of("error", "Email ou mot de passe incorrect"));
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        log.info("Connexion réussie pour : {}", user.getEmail());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", user.getEmail(),
                "role", user.getRole()
        ));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class LoginRequest {
        private String email;
        private String password;
    }
}
