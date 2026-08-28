package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.StanDTO;
import com.efacility.ticketing.dto.request.CreateStanRequest;
import com.efacility.ticketing.dto.request.UpdateStanRequest;
import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.mapper.StanMapper;
import com.efacility.ticketing.model.Stan;
import com.efacility.ticketing.model.Zgrada;
import com.efacility.ticketing.repository.StanRepository;
import com.efacility.ticketing.repository.TiketRepository;
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
    private final TiketRepository ticketRepository;
    private final StanMapper apartmentMapper;

    public StanService(StanRepository apartmentRepository,
                            ZgradaRepository buildingRepository,
                            TiketRepository ticketRepository,
                            StanMapper apartmentMapper) {
        this.apartmentRepository = apartmentRepository;
        this.buildingRepository = buildingRepository;
        this.ticketRepository = ticketRepository;
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
            throw new ResourceNotFoundException("Zgrada nije pronađena, id: " + buildingId);
        }
        return apartmentRepository.findByBuildingId(buildingId)
                .stream()
                .map(apartmentMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StanDTO getStan(Long id) {
        Stan apartment = apartmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stan nije pronađen, id: " + id));
        return apartmentMapper.toDomainDTO(apartment);
    }

    public StanDTO addStan(CreateStanRequest request) {
        Zgrada building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Zgrada nije pronađena, id: " + request.getBuildingId()));
        Stan apartment = new Stan();
        apartment.setNumber(request.getNumber());
        apartment.setFloor(request.getFloor());
        apartment.setBuilding(building);
        Stan saved = apartmentRepository.save(apartment);
        return apartmentMapper.toDomainDTO(saved);
    }

    public StanDTO updateStan(UpdateStanRequest request) {
        Stan apartment = apartmentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Stan nije pronađen, id: " + request.getId()));
        Zgrada building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Zgrada nije pronađena, id: " + request.getBuildingId()));
        apartment.setNumber(request.getNumber());
        apartment.setFloor(request.getFloor());
        apartment.setBuilding(building);
        Stan saved = apartmentRepository.save(apartment);
        return apartmentMapper.toDomainDTO(saved);
    }

    public String deleteStan(Long id) {
        if (!apartmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Stan nije pronađen, id: " + id);
        }
        if (ticketRepository.existsByApartmentId(id)) {
            throw new IllegalArgumentException(
                    "Stan se ne može obrisati: za njega postoje prijavljeni tiketi.");
        }
        apartmentRepository.deleteById(id);
        return "Stan je uspešno obrisan!";
    }
}
