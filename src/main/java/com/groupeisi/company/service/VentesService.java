package com.groupeisi.company.service;

import com.groupeisi.company.dto.VentesDto;
import com.groupeisi.company.entities.Produits;
import com.groupeisi.company.entities.UserAccount;
import com.groupeisi.company.entities.Ventes;
import com.groupeisi.company.mapper.VentesMapper;
import com.groupeisi.company.repository.ProduitsRepository;
import com.groupeisi.company.repository.UserAccountRepository;
import com.groupeisi.company.repository.VentesRepository;
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
public class VentesService {

    private final VentesRepository ventesRepository;
    private final ProduitsRepository produitsRepository;
    private final UserAccountRepository userAccountRepository;
    private final VentesMapper ventesMapper;

    @Cacheable(value = "ventes")
    public List<VentesDto> findAll() {
        log.info("Récupération de toutes les ventes");
        return ventesMapper.toDtoList(ventesRepository.findAll());
    }

    @Cacheable(value = "ventes", key = "#id")
    public VentesDto findById(Long id) {
        log.info("Récupération de la vente id : {}", id);
        return ventesRepository.findById(id)
                .map(ventesMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Vente introuvable id : " + id));
    }

    @CacheEvict(value = "ventes", allEntries = true)
    public VentesDto create(VentesDto dto) {
        log.info("Création d'une vente pour produit ref={} par user id={}", dto.getProductRef(), dto.getUserId());
        Produits produit = produitsRepository.findById(dto.getProductRef())
                .orElseThrow(() -> new RuntimeException("Produit introuvable ref : " + dto.getProductRef()));
        UserAccount user = userAccountRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable id : " + dto.getUserId()));

        // Mise à jour du stock après vente
        produit.setStock(produit.getStock() + dto.getQuantity());
        produitsRepository.save(produit);

        Ventes vente = Ventes.builder()
                .dateP(dto.getDateP() != null ? dto.getDateP() : new Date())
                .quantity(dto.getQuantity())
                .product(produit)
                .user(user)
                .build();
        Ventes saved = ventesRepository.save(vente);
        log.info("Vente créée avec succès id={}", saved.getId());
        return ventesMapper.toDto(saved);
    }

    @CacheEvict(value = "ventes", allEntries = true)
    public VentesDto update(Long id, VentesDto dto) {
        log.info("Mise à jour de la vente id : {}", id);
        Ventes existing = ventesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vente introuvable id : " + id));
        existing.setQuantity(dto.getQuantity());
        if (dto.getDateP() != null) existing.setDateP(dto.getDateP());
        return ventesMapper.toDto(ventesRepository.save(existing));
    }

    @CacheEvict(value = "ventes", allEntries = true)
    public void delete(Long id) {
        log.warn("Suppression de la vente id : {}", id);
        if (!ventesRepository.existsById(id)) {
            throw new RuntimeException("Vente introuvable id : " + id);
        }
        ventesRepository.deleteById(id);
    }
}
