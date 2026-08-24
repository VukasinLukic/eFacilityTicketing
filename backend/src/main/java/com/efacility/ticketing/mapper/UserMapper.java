package com.efacility.ticketing.mapper;

import com.efacility.ticketing.dto.UserDTO;
import com.efacility.ticketing.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<UserDTO, User> {

    @Override
    UserDTO toDomainDTO(User entity);

    @Override
    @Mapping(target = "password", ignore = true)
    User toDomainEntity(UserDTO dto);
}
