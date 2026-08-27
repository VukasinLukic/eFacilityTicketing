package com.efacility.ticketing.mapper;

import com.efacility.ticketing.dto.IstorijaTiketaDTO;
import com.efacility.ticketing.model.IstorijaTiketa;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {KorisnikMapper.class})
public interface IstorijaTiketaMapper {

    IstorijaTiketaDTO toDomainDTO(IstorijaTiketa entity);
}
