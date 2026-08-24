package com.rentaCar.rentaCarBackend.mapper;

import com.rentaCar.rentaCarBackend.dto.CarModelDTO;
import com.rentaCar.rentaCarBackend.model.CarModel;
import org.springframework.stereotype.Component;

@Component
public class CarModelMapper {

    public CarModelDTO toDomainDTO(CarModel entity) {
        if (entity == null) return null;

        CarModelDTO dto = new CarModelDTO();
        dto.setId(entity.getId());
        dto.setMake(entity.getMake());
        dto.setName(entity.getName());
        dto.setEngineDescription(entity.getEngineDescription());
        dto.setCategory(entity.getCategory());
        dto.setSeats(entity.getSeats());
        dto.setTransmission(entity.getTransmission());
        dto.setFuelType(entity.getFuelType());
        dto.setDoors(entity.getDoors());
        dto.setLuggageCapacity(entity.getLuggageCapacity());
        dto.setHasAc(entity.isHasAc());
        dto.setPictureUrl(entity.getPictureUrl());
        dto.setLogoUrl(entity.getLogoUrl());
        dto.setPriceTier1to3(entity.getPriceTier1to3());
        dto.setPriceTier4to7(entity.getPriceTier4to7());
        dto.setPriceTier8to15(entity.getPriceTier8to15());
        dto.setPriceTier16to30(entity.getPriceTier16to30());
        dto.setPriceTier31to45(entity.getPriceTier31to45());
        dto.setPriceTier46plus(entity.getPriceTier46plus());

        return dto;
    }

    public CarModel toDomainEntity(CarModelDTO dto) {
        if (dto == null) return null;

        CarModel entity = new CarModel();
        // ID se obično ne postavlja ručno jer se generiše, osim kod ažuriranja
        if (dto.getId() != 0) {
            entity.setId(dto.getId());
        }
        entity.setMake(dto.getMake());
        entity.setName(dto.getName());
        entity.setEngineDescription(dto.getEngineDescription());
        entity.setCategory(dto.getCategory());
        entity.setSeats(dto.getSeats());
        entity.setTransmission(dto.getTransmission());
        entity.setFuelType(dto.getFuelType());
        entity.setDoors(dto.getDoors());
        entity.setLuggageCapacity(dto.getLuggageCapacity());
        entity.setHasAc(dto.isHasAc());
        entity.setPictureUrl(dto.getPictureUrl());
        entity.setLogoUrl(dto.getLogoUrl());
        entity.setPriceTier1to3(dto.getPriceTier1to3());
        entity.setPriceTier4to7(dto.getPriceTier4to7());
        entity.setPriceTier8to15(dto.getPriceTier8to15());
        entity.setPriceTier16to30(dto.getPriceTier16to30());
        entity.setPriceTier31to45(dto.getPriceTier31to45());
        entity.setPriceTier46plus(dto.getPriceTier46plus());

        return entity;
    }
}