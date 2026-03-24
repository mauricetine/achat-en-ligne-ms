package com.groupeisi.company.service;

import com.groupeisi.company.dto.ProduitsDto;
import com.groupeisi.company.entities.Produits;
import com.groupeisi.company.entities.UserAccount;
import com.groupeisi.company.mapper.ProduitsMapper;
import com.groupeisi.company.repository.ProduitsRepository;
import com.groupeisi.company.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProduitsService {

    private final ProduitsRepository produitsRepository;
    private final UserAccountRepository userAccountRepository;
    private final ProduitsMapper produitsMapper;

    @Cacheable(value = "produits")
    public List<ProduitsDto> findAll() {
        log.info("Récupération de tous les produits");
        return produitsMapper.toDtoList(produitsRepository.findAll());
    }

    @Cacheable(value = "produits", key = "#ref")
    public ProduitsDto findByRef(String ref) {
        log.info("Récupération du produit avec la ref : {}", ref);
        return produitsRepository.findById(ref)
                .map(produitsMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Produit introuvable avec la ref : {}", ref);
                    return new RuntimeException("Produit introuvable avec la ref : " + ref);
                });
    }

    @CacheEvict(value = "produits", allEntries = true)
    public ProduitsDto create(ProduitsDto dto) {
        log.info("Création du produit avec ref : {}", dto.getRef());
        UserAccount user = userAccountRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable id : " + dto.getUserId()));
        Produits produit = Produits.builder()
                .ref(dto.getRef())
                .name(dto.getName())
                .stock(dto.getStock())
                .user(user)
                .build();
        return produitsMapper.toDto(produitsRepository.save(produit));
    }

    @CacheEvict(value = "produits", allEntries = true)
    public ProduitsDto update(String ref, ProduitsDto dto) {
        log.info("Mise à jour du produit ref : {}", ref);
        Produits existing = produitsRepository.findById(ref)
                .orElseThrow(() -> new RuntimeException("Produit introuvable avec la ref : " + ref));
        existing.setName(dto.getName());
        existing.setStock(dto.getStock());
        if (dto.getUserId() != null) {
            UserAccount user = userAccountRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable id : " + dto.getUserId()));
            existing.setUser(user);
        }
        return produitsMapper.toDto(produitsRepository.save(existing));
    }

    @CacheEvict(value = "produits", allEntries = true)
    public void delete(String ref) {
        log.warn("Suppression du produit ref : {}", ref);
        if (!produitsRepository.existsById(ref)) {
            throw new RuntimeException("Produit introuvable avec la ref : " + ref);
        }
        produitsRepository.deleteById(ref);
        log.info("Produit ref={} supprimé avec succès", ref);
    }
}
