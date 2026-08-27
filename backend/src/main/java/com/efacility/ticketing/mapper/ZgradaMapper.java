package com.efacility.ticketing.mapper;

import com.efacility.ticketing.dto.ZgradaDTO;
import com.efacility.ticketing.model.Zgrada;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ZgradaMapper extends BaseMapper<ZgradaDTO, Zgrada> {

    @Override
    ZgradaDTO toDomainDTO(Zgrada entity);

    @Override
    Zgrada toDomainEntity(ZgradaDTO dto);
}
