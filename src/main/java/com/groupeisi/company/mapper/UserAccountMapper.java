package com.groupeisi.company.mapper;

import com.groupeisi.company.dto.UserAccountDto;
import com.groupeisi.company.entities.UserAccount;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {

    UserAccountDto toDto(UserAccount entity);

    UserAccount toEntity(UserAccountDto dto);

    List<UserAccountDto> toDtoList(List<UserAccount> entities);
}
