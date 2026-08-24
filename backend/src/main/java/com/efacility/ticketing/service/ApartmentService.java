package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.ApartmentDTO;
import com.efacility.ticketing.dto.request.CreateApartmentRequest;
import com.efacility.ticketing.dto.request.UpdateApartmentRequest;
import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.mapper.ApartmentMapper;
import com.efacility.ticketing.model.Apartment;
import com.efacility.ticketing.model.Building;
import com.efacility.ticketing.repository.ApartmentRepository;
import com.efacility.ticketing.repository.BuildingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final BuildingRepository buildingRepository;
    private final ApartmentMapper apartmentMapper;

    public ApartmentService(ApartmentRepository apartmentRepository,
                            BuildingRepository buildingRepository,
                            ApartmentMapper apartmentMapper) {
        this.apartmentRepository = apartmentRepository;
        this.buildingRepository = buildingRepository;
        this.apartmentMapper = apartmentMapper;
    }

    @Transactional(readOnly = true)
    public List<ApartmentDTO> getAll() {
        return apartmentRepository.findAll()
                .stream()
                .map(apartmentMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApartmentDTO> getByBuilding(Long buildingId) {
        if (!buildingRepository.existsById(buildingId)) {
            throw new ResourceNotFoundException("Building not found with id: " + buildingId);
        }
        return apartmentRepository.findByBuildingId(buildingId)
                .stream()
                .map(apartmentMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApartmentDTO getApartment(Long id) {
        Apartment apartment = apartmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + id));
        return apartmentMapper.toDomainDTO(apartment);
    }

    public ApartmentDTO addApartment(CreateApartmentRequest request) {
        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + request.getBuildingId()));
        Apartment apartment = new Apartment();
        apartment.setNumber(request.getNumber());
        apartment.setFloor(request.getFloor());
        apartment.setBuilding(building);
        Apartment saved = apartmentRepository.save(apartment);
        return apartmentMapper.toDomainDTO(saved);
    }

    public ApartmentDTO updateApartment(UpdateApartmentRequest request) {
        Apartment apartment = apartmentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + request.getId()));
        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + request.getBuildingId()));
        apartment.setNumber(request.getNumber());
        apartment.setFloor(request.getFloor());
        apartment.setBuilding(building);
        Apartment saved = apartmentRepository.save(apartment);
        return apartmentMapper.toDomainDTO(saved);
    }

    public String deleteApartment(Long id) {
        if (!apartmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Apartment not found with id: " + id);
        }
        apartmentRepository.deleteById(id);
        return "Apartment deleted successfully!";
    }
}
