package com.efacility.ticketing.mapper;

import com.efacility.ticketing.dto.BuildingDTO;
import com.efacility.ticketing.model.Building;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BuildingMapper extends BaseMapper<BuildingDTO, Building> {

    @Override
    BuildingDTO toDomainDTO(Building entity);

    @Override
    Building toDomainEntity(BuildingDTO dto);
}
