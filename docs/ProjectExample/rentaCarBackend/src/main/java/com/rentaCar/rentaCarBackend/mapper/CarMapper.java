package com.rentaCar.rentaCarBackend.mapper;

import com.rentaCar.rentaCarBackend.dto.CarDTO;
import com.rentaCar.rentaCarBackend.model.Car;
import com.rentaCar.rentaCarBackend.model.CarModel;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {

    private final CarModelMapper carModelMapper;

    public CarMapper(CarModelMapper carModelMapper) {
        this.carModelMapper = carModelMapper;
    }

    public Car toDomainEntity(CarDTO carDTO, CarModel carModel) {
        if (carDTO == null) return null;

        Car car = new Car();
        // VAŽNO: Pretpostavka je da CarDTO.getId() vraća 0 ako ID nije postavljen
        // za primitivne int tipove. Ako je u CarDTO Integer id, onda bi trebalo
        // da proveravate if (carDTO.getId() != null)
        if (carDTO.getId() != 0) {
            car.setId(carDTO.getId());
        }
        car.setLicensePlate(carDTO.getLicensePlate());
        car.setYear(carDTO.getYear());
        car.setStatus(carDTO.getStatus());
        car.setNotes(carDTO.getNotes());
        car.setCarModel(carModel);

        return car;
    }

    public CarDTO toDomainDTO(Car car) {
        if (car == null) return null;

        CarDTO carDTO = new CarDTO();
        carDTO.setId(car.getId());
        carDTO.setLicensePlate(car.getLicensePlate());
        carDTO.setYear(car.getYear());
        carDTO.setStatus(car.getStatus());
        carDTO.setNotes(car.getNotes());

        // Koristimo CarModelMapper da pretvorimo ugnježdeni entitet u DTO
        if (car.getCarModel() != null) {
            carDTO.setCarModel(carModelMapper.toDomainDTO(car.getCarModel()));
        }

        return carDTO;
    }
}