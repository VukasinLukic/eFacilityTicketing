package com.efacility.ticketing.mapper;

import com.efacility.ticketing.dto.KorisnikDTO;
import com.efacility.ticketing.model.Korisnik;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KorisnikMapper extends BaseMapper<KorisnikDTO, Korisnik> {

    @Override
    KorisnikDTO toDomainDTO(Korisnik entity);

    @Override
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "apartments", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    Korisnik toDomainEntity(KorisnikDTO dto);
}
