package com.rentaCar.rentaCarBackend.service;

import com.rentaCar.rentaCarBackend.JPARepo.CarModelRepository;
import com.rentaCar.rentaCarBackend.dto.CarModelDTO;
import com.rentaCar.rentaCarBackend.mapper.CarModelMapper;
import com.rentaCar.rentaCarBackend.model.CarModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarModelService {

    private final CarModelRepository carModelRepo;
    private final CarModelMapper carModelMapper;

    public CarModelService(CarModelRepository carModelRepo, CarModelMapper carModelMapper) {
        this.carModelRepo = carModelRepo;
        this.carModelMapper = carModelMapper;
    }

    public List<CarModelDTO> getAll() {
        List<CarModel> carModels = carModelRepo.findAll();
        return carModels.stream()
                .map(carModelMapper::toDomainDTO)
                .collect(Collectors.toList());
    }
}