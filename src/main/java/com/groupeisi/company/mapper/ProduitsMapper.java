package com.groupeisi.company.mapper;

import com.groupeisi.company.dto.ProduitsDto;
import com.groupeisi.company.entities.Produits;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProduitsMapper {

    @Mapping(source = "user.id", target = "userId")
    ProduitsDto toDto(Produits entity);

    @Mapping(source = "userId", target = "user.id")
    Produits toEntity(ProduitsDto dto);

    List<ProduitsDto> toDtoList(List<Produits> entities);
}
