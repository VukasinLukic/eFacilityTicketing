package com.rentaCar.rentaCarBackend.mapper;

import com.rentaCar.rentaCarBackend.dto.CityDTO;
import com.rentaCar.rentaCarBackend.model.City;
import org.springframework.stereotype.Component;

@Component
// IZMENJENO: BaseMapper sada radi sa CityDTO
public class CityMapper implements BaseMapper<CityDTO, City> {

    @Override
    public CityDTO toDomainDTO(City domainEntity) {
        CityDTO cityDTO = new CityDTO();
        cityDTO.setId(domainEntity.getId());
        cityDTO.setName(domainEntity.getName());
        return cityDTO;
    }

    @Override
    public City toDomainEntity(CityDTO domainDTO) {
        City city = new City();
        city.setId(domainDTO.getId());
        city.setName(domainDTO.getName());
        return city;
    }
}