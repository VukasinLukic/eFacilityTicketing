/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rentaCar.rentaCarBackend.mapper;

import com.rentaCar.rentaCarBackend.dto.UserDTO;
import com.rentaCar.rentaCarBackend.model.User;
import org.springframework.stereotype.Component;

/**
 *
 * @author Saki
 */
@Component
public class UserMapper implements BaseMapper<UserDTO, User> {

    @Override
    public UserDTO toDomainDTO(User DomainEntity) {
        UserDTO user = new UserDTO();
        user.setId(DomainEntity.getId());
        user.setName(DomainEntity.getName());
        user.setSurname(DomainEntity.getSurname());
        user.setUsername(DomainEntity.getUsername());
        user.setPassword(DomainEntity.getPassword());
        return user;
    }

    @Override
    public User toDomainEntity(UserDTO DomainDTO) {
        User u = new User();
        u.setId(DomainDTO.getId());
        u.setName(DomainDTO.getName());
        u.setSurname(DomainDTO.getSurname());
        u.setUsername(DomainDTO.getUsername());
        u.setPassword(DomainDTO.getPassword());
        return u;
    }

}
