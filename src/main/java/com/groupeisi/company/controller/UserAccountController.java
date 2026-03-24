package com.groupeisi.company.controller;

import com.groupeisi.company.dto.UserAccountDto;
import com.groupeisi.company.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "UserAccount", description = "Gestion des utilisateurs")
public class UserAccountController {

    private final UserAccountService userAccountService;

    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs")
    public ResponseEntity<List<UserAccountDto>> findAll() {
        return ResponseEntity.ok(userAccountService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur par son id")
    public ResponseEntity<UserAccountDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userAccountService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un nouvel utilisateur")
    public ResponseEntity<UserAccountDto> create(@RequestBody UserAccountDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userAccountService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un utilisateur")
    public ResponseEntity<UserAccountDto> update(@PathVariable Long id, @RequestBody UserAccountDto dto) {
        return ResponseEntity.ok(userAccountService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userAccountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
