package com.efacility.ticketing.mapper;

import com.efacility.ticketing.dto.StanDTO;
import com.efacility.ticketing.model.Stan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ZgradaMapper.class})
public interface StanMapper extends BaseMapper<StanDTO, Stan> {

    @Override
    StanDTO toDomainDTO(Stan entity);

    @Override
    Stan toDomainEntity(StanDTO dto);
}
