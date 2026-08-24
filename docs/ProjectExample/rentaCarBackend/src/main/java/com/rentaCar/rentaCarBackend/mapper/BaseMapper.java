/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.rentaCar.rentaCarBackend.mapper;

import com.rentaCar.rentaCarBackend.dto.DomainDTO;
import com.rentaCar.rentaCarBackend.model.DomainEntity;

/**
 *
 * @author Saki
 */
public interface BaseMapper<DTO extends DomainDTO, DB extends DomainEntity> {

    public DTO toDomainDTO(DB DomainEntity);

    public DB toDomainEntity(DTO DomainDTO);

}
