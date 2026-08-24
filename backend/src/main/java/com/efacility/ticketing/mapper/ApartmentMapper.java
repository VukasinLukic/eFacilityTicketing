package com.efacility.ticketing.mapper;

import com.efacility.ticketing.dto.ApartmentDTO;
import com.efacility.ticketing.model.Apartment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {BuildingMapper.class})
public interface ApartmentMapper extends BaseMapper<ApartmentDTO, Apartment> {

    @Override
    ApartmentDTO toDomainDTO(Apartment entity);

    @Override
    Apartment toDomainEntity(ApartmentDTO dto);
}
