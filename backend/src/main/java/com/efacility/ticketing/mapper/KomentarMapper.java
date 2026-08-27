package com.efacility.ticketing.mapper;

import com.efacility.ticketing.dto.KomentarDTO;
import com.efacility.ticketing.model.Komentar;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {KorisnikMapper.class})
public interface KomentarMapper {

    KomentarDTO toDomainDTO(Komentar entity);
}
