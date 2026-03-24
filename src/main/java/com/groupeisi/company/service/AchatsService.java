package com.groupeisi.company.service;

import com.groupeisi.company.dto.AchatsDto;
import com.groupeisi.company.entities.Achats;
import com.groupeisi.company.entities.Produits;
import com.groupeisi.company.entities.UserAccount;
import com.groupeisi.company.mapper.AchatsMapper;
import com.groupeisi.company.repository.AchatsRepository;
import com.groupeisi.company.repository.ProduitsRepository;
import com.groupeisi.company.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AchatsService {

    private final AchatsRepository achatsRepository;
    private final ProduitsRepository produitsRepository;
    private final UserAccountRepository userAccountRepository;
    private final AchatsMapper achatsMapper;

    @Cacheable(value = "achats")
    public List<AchatsDto> findAll() {
        log.info("Récupération de tous les achats");
        return achatsMapper.toDtoList(achatsRepository.findAll());
    }

    @Cacheable(value = "achats", key = "#id")
    public AchatsDto findById(Long id) {
        log.info("Récupération de l'achat id : {}", id);
        return achatsRepository.findById(id)
                .map(achatsMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Achat introuvable id : " + id));
    }

    @CacheEvict(value = "achats", allEntries = true)
    public AchatsDto create(AchatsDto dto) {
        log.info("Création d'un achat pour produit ref={} par user id={}", dto.getProductRef(), dto.getUserId());
        Produits produit = produitsRepository.findById(dto.getProductRef())
                .orElseThrow(() -> new RuntimeException("Produit introuvable ref : " + dto.getProductRef()));
        UserAccount user = userAccountRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable id : " + dto.getUserId()));

        // Vérification du stock
        if (produit.getStock() < dto.getQuantity()) {
            throw new RuntimeException("Stock insuffisant pour le produit : " + produit.getRef());
        }
        produit.setStock(produit.getStock() - dto.getQuantity());
        produitsRepository.save(produit);

        Achats achat = Achats.builder()
                .dateP(dto.getDateP() != null ? dto.getDateP() : new Date())
                .quantity(dto.getQuantity())
                .product(produit)
                .user(user)
                .build();
        Achats saved = achatsRepository.save(achat);
        log.info("Achat créé avec succès id={}", saved.getId());
        return achatsMapper.toDto(saved);
    }

    @CacheEvict(value = "achats", allEntries = true)
    public AchatsDto update(Long id, AchatsDto dto) {
        log.info("Mise à jour de l'achat id : {}", id);
        Achats existing = achatsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Achat introuvable id : " + id));
        existing.setQuantity(dto.getQuantity());
        if (dto.getDateP() != null) existing.setDateP(dto.getDateP());
        return achatsMapper.toDto(achatsRepository.save(existing));
    }

    @CacheEvict(value = "achats", allEntries = true)
    public void delete(Long id) {
        log.warn("Suppression de l'achat id : {}", id);
        if (!achatsRepository.existsById(id)) {
            throw new RuntimeException("Achat introuvable id : " + id);
        }
        achatsRepository.deleteById(id);
    }
}
