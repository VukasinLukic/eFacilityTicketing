package com.efacility.ticketing.mapper;

import com.efacility.ticketing.dto.DomainDTO;
import com.efacility.ticketing.model.DomainEntity;

public interface BaseMapper<DTO extends DomainDTO, DB extends DomainEntity> {

    DTO toDomainDTO(DB entity);

    DB toDomainEntity(DTO dto);
}
