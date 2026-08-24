package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.BuildingDTO;
import com.efacility.ticketing.dto.request.CreateBuildingRequest;
import com.efacility.ticketing.dto.request.UpdateBuildingRequest;
import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.mapper.BuildingMapper;
import com.efacility.ticketing.model.Building;
import com.efacility.ticketing.repository.ApartmentRepository;
import com.efacility.ticketing.repository.BuildingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final ApartmentRepository apartmentRepository;
    private final BuildingMapper buildingMapper;

    public BuildingService(BuildingRepository buildingRepository,
                           ApartmentRepository apartmentRepository,
                           BuildingMapper buildingMapper) {
        this.buildingRepository = buildingRepository;
        this.apartmentRepository = apartmentRepository;
        this.buildingMapper = buildingMapper;
    }

    @Transactional(readOnly = true)
    public List<BuildingDTO> getAll() {
        return buildingRepository.findAll()
                .stream()
                .map(buildingMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BuildingDTO getBuilding(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));
        return buildingMapper.toDomainDTO(building);
    }

    public BuildingDTO addBuilding(CreateBuildingRequest request) {
        Building building = new Building();
        building.setName(request.getName());
        building.setAddress(request.getAddress());
        Building saved = buildingRepository.save(building);
        return buildingMapper.toDomainDTO(saved);
    }

    public BuildingDTO updateBuilding(UpdateBuildingRequest request) {
        Building building = buildingRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + request.getId()));
        building.setName(request.getName());
        building.setAddress(request.getAddress());
        Building saved = buildingRepository.save(building);
        return buildingMapper.toDomainDTO(saved);
    }

    public String deleteBuilding(Long id) {
        if (!buildingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Building not found with id: " + id);
        }
        if (!apartmentRepository.findByBuildingId(id).isEmpty()) {
            throw new IllegalArgumentException("Cannot delete building that has apartments. Delete apartments first.");
        }
        buildingRepository.deleteById(id);
        return "Building deleted successfully!";
    }
}
