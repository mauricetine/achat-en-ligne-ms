package com.groupeisi.company.mapper;

import com.groupeisi.company.dto.AchatsDto;
import com.groupeisi.company.entities.Achats;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AchatsMapper {

    @Mapping(source = "product.ref", target = "productRef")
    @Mapping(source = "user.id", target = "userId")
    AchatsDto toDto(Achats entity);

    @Mapping(source = "productRef", target = "product.ref")
    @Mapping(source = "userId", target = "user.id")
    Achats toEntity(AchatsDto dto);

    List<AchatsDto> toDtoList(List<Achats> entities);
}
