package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.ZgradaDTO;
import com.efacility.ticketing.dto.request.CreateZgradaRequest;
import com.efacility.ticketing.dto.request.UpdateZgradaRequest;
import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.mapper.ZgradaMapper;
import com.efacility.ticketing.model.Zgrada;
import com.efacility.ticketing.repository.TiketRepository;
import com.efacility.ticketing.repository.ZgradaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ZgradaService {

    private final ZgradaRepository buildingRepository;
    private final TiketRepository ticketRepository;
    private final ZgradaMapper buildingMapper;

    public ZgradaService(ZgradaRepository buildingRepository,
                           TiketRepository ticketRepository,
                           ZgradaMapper buildingMapper) {
        this.buildingRepository = buildingRepository;
        this.ticketRepository = ticketRepository;
        this.buildingMapper = buildingMapper;
    }

    @Transactional(readOnly = true)
    public List<ZgradaDTO> getAll() {
        return buildingRepository.findAll()
                .stream()
                .map(buildingMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ZgradaDTO getZgrada(Long id) {
        Zgrada building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zgrada nije pronađena, id: " + id));
        return buildingMapper.toDomainDTO(building);
    }

    public ZgradaDTO addZgrada(CreateZgradaRequest request) {
        Zgrada building = new Zgrada();
        building.setName(request.getName());
        building.setAddress(request.getAddress());
        Zgrada saved = buildingRepository.save(building);
        return buildingMapper.toDomainDTO(saved);
    }

    public ZgradaDTO updateZgrada(UpdateZgradaRequest request) {
        Zgrada building = buildingRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Zgrada nije pronađena, id: " + request.getId()));
        building.setName(request.getName());
        building.setAddress(request.getAddress());
        Zgrada saved = buildingRepository.save(building);
        return buildingMapper.toDomainDTO(saved);
    }

    public String deleteZgrada(Long id) {
        Zgrada building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zgrada nije pronađena, id: " + id));

        if (ticketRepository.existsByApartment_Building_Id(id)) {
            throw new IllegalArgumentException(
                    "Zgrada se ne može obrisati: neki stanovi imaju vezane tikete.");
        }

        buildingRepository.delete(building);
        return "Zgrada je uspešno obrisana!";
    }
}
