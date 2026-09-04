package com.efacility.ticketing.mapper;

import com.efacility.ticketing.dto.ZgradaDTO;
import com.efacility.ticketing.model.Zgrada;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ZgradaMapper extends BaseMapper<ZgradaDTO, Zgrada> {

    @Override
    ZgradaDTO toDomainDTO(Zgrada entity);

    @Override
    @Mapping(target = "apartments", ignore = true)
    Zgrada toDomainEntity(ZgradaDTO dto);
}
