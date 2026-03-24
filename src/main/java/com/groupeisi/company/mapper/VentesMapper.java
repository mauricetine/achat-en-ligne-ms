package com.groupeisi.company.mapper;

import com.groupeisi.company.dto.VentesDto;
import com.groupeisi.company.entities.Ventes;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VentesMapper {

    @Mapping(source = "product.ref", target = "productRef")
    @Mapping(source = "user.id", target = "userId")
    VentesDto toDto(Ventes entity);

    @Mapping(source = "productRef", target = "product.ref")
    @Mapping(source = "userId", target = "user.id")
    Ventes toEntity(VentesDto dto);

    List<VentesDto> toDtoList(List<Ventes> entities);
}
