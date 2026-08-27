package com.efacility.ticketing.mapper;

import com.efacility.ticketing.dto.TiketDTO;
import com.efacility.ticketing.model.Tiket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {KorisnikMapper.class, StanMapper.class})
public interface TiketMapper {

    TiketDTO toDomainDTO(Tiket entity);
}
