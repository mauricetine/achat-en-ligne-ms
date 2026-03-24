package com.groupeisi.company.service;

import com.groupeisi.company.dto.UserAccountDto;
import com.groupeisi.company.entities.UserAccount;
import com.groupeisi.company.mapper.UserAccountMapper;
import com.groupeisi.company.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final UserAccountMapper userAccountMapper;
    private final PasswordEncoder passwordEncoder;

    @Cacheable(value = "users")
    public List<UserAccountDto> findAll() {
        log.info("Récupération de tous les utilisateurs");
        return userAccountMapper.toDtoList(userAccountRepository.findAll());
    }

    @Cacheable(value = "users", key = "#id")
    public UserAccountDto findById(Long id) {
        log.info("Récupération de l'utilisateur avec l'id : {}", id);
        UserAccount user = userAccountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Utilisateur introuvable avec l'id : {}", id);
                    return new RuntimeException("Utilisateur introuvable avec l'id : " + id);
                });
        return userAccountMapper.toDto(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public UserAccountDto create(UserAccountDto dto) {
        log.info("Création d'un nouvel utilisateur avec email : {}", dto.getEmail());
        if (userAccountRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email déjà utilisé : " + dto.getEmail());
        }
        UserAccount user = userAccountMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        UserAccount saved = userAccountRepository.save(user);
        log.info("Utilisateur créé avec succès, id : {}", saved.getId());
        return userAccountMapper.toDto(saved);
    }

    @CacheEvict(value = "users", allEntries = true)
    public UserAccountDto update(Long id, UserAccountDto dto) {
        log.info("Mise à jour de l'utilisateur avec l'id : {}", id);
        UserAccount existing = userAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'id : " + id));
        existing.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        existing.setRole(dto.getRole() != null ? dto.getRole() : existing.getRole());
        return userAccountMapper.toDto(userAccountRepository.save(existing));
    }

    @CacheEvict(value = "users", allEntries = true)
    public void delete(Long id) {
        log.warn("Suppression de l'utilisateur avec l'id : {}", id);
        if (!userAccountRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur introuvable avec l'id : " + id);
        }
        userAccountRepository.deleteById(id);
        log.info("Utilisateur id={} supprimé avec succès", id);
    }
}
