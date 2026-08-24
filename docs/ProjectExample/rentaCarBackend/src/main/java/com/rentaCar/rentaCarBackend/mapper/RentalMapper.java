package com.rentaCar.rentaCarBackend.mapper;

import com.rentaCar.rentaCarBackend.dto.RentalDTO;
import com.rentaCar.rentaCarBackend.model.Car;
import com.rentaCar.rentaCarBackend.model.Client;
import com.rentaCar.rentaCarBackend.model.Rental;
import com.rentaCar.rentaCarBackend.model.User;
import org.springframework.stereotype.Component;

@Component
public class RentalMapper {

    // 1. Deklarišemo mapere koji su nam potrebni
    private final CarMapper carMapper;
    private final ClientMapper clientMapper;
    private final UserMapper userMapper;

    // 2. Koristimo konstruktor da nam Spring automatski dodeli instance
    public RentalMapper(CarMapper carMapper, ClientMapper clientMapper, UserMapper userMapper) {
        this.carMapper = carMapper;
        this.clientMapper = clientMapper;
        this.userMapper = userMapper;
    }

    // Metoda za pretvaranje DTO u Entitet
    // Napomena: Ova metoda je pojednostavljena. Kompletan entitet se sklapa u servisnom sloju.
    public Rental toDomainEntity(RentalDTO dto, Car car, Client client, User user) {
        if (dto == null) {
            return null;
        }
        Rental entity = new Rental();
        // VAŽNO: Pretpostavka je da RentalDTO.getRentalId() vraća 0 ako ID nije postavljen
        // za primitivne int tipove. Ako je u RentalDTO Integer id, onda bi trebalo
        // da proveravate if (dto.getRentalId() != null)
        if (dto.getRentalId() != 0) {
            entity.setRentalId(dto.getRentalId());
        }
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setNotes(dto.getNotes());
        entity.setStatus(dto.getStatus());

        // Postavljamo već pronađene entitete
        entity.setCar(car);
        entity.setClient(client);
        entity.setUser(user);

        return entity;
    }

    // Metoda za pretvaranje Entiteta u DTO
    public RentalDTO toDomainDTO(Rental entity) {
        if (entity == null) {
            return null;
        }
        RentalDTO dto = new RentalDTO();
        dto.setRentalId(entity.getRentalId());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setNotes(entity.getNotes());
        dto.setStatus(entity.getStatus());

        // Koristimo injektovane mapere za umetnute objekte
        if (entity.getCar() != null) {
            dto.setCar(carMapper.toDomainDTO(entity.getCar()));
        }
        if (entity.getClient() != null) {
            dto.setClient(clientMapper.toDomainDTO(entity.getClient()));
        }
        if (entity.getUser() != null) {
            dto.setUser(userMapper.toDomainDTO(entity.getUser()));
        }

        return dto;
    }
}