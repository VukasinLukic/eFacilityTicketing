package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.StanDTO;
import com.efacility.ticketing.dto.request.CreateStanRequest;
import com.efacility.ticketing.dto.request.UpdateStanRequest;
import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.mapper.StanMapper;
import com.efacility.ticketing.model.Stan;
import com.efacility.ticketing.model.Zgrada;
import com.efacility.ticketing.repository.StanRepository;
import com.efacility.ticketing.repository.ZgradaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StanService {

    private final StanRepository apartmentRepository;
    private final ZgradaRepository buildingRepository;
    private final StanMapper apartmentMapper;

    public StanService(StanRepository apartmentRepository,
                            ZgradaRepository buildingRepository,
                            StanMapper apartmentMapper) {
        this.apartmentRepository = apartmentRepository;
        this.buildingRepository = buildingRepository;
        this.apartmentMapper = apartmentMapper;
    }

    @Transactional(readOnly = true)
    public List<StanDTO> getAll() {
        return apartmentRepository.findAll()
                .stream()
                .map(apartmentMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StanDTO> getByZgrada(Long buildingId) {
        if (!buildingRepository.existsById(buildingId)) {
            throw new ResourceNotFoundException("Building not found with id: " + buildingId);
        }
        return apartmentRepository.findByBuildingId(buildingId)
                .stream()
                .map(apartmentMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StanDTO getStan(Long id) {
        Stan apartment = apartmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + id));
        return apartmentMapper.toDomainDTO(apartment);
    }

    public StanDTO addStan(CreateStanRequest request) {
        Zgrada building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + request.getBuildingId()));
        Stan apartment = new Stan();
        apartment.setNumber(request.getNumber());
        apartment.setFloor(request.getFloor());
        apartment.setBuilding(building);
        Stan saved = apartmentRepository.save(apartment);
        return apartmentMapper.toDomainDTO(saved);
    }

    public StanDTO updateStan(UpdateStanRequest request) {
        Stan apartment = apartmentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + request.getId()));
        Zgrada building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + request.getBuildingId()));
        apartment.setNumber(request.getNumber());
        apartment.setFloor(request.getFloor());
        apartment.setBuilding(building);
        Stan saved = apartmentRepository.save(apartment);
        return apartmentMapper.toDomainDTO(saved);
    }

    public String deleteStan(Long id) {
        if (!apartmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Apartment not found with id: " + id);
        }
        apartmentRepository.deleteById(id);
        return "Apartment deleted successfully!";
    }
}
