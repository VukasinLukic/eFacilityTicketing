package com.efacility.ticketing.service.impl;

import com.efacility.ticketing.dto.ZgradaDTO;
import com.efacility.ticketing.dto.request.CreateZgradaRequest;
import com.efacility.ticketing.dto.request.UpdateZgradaRequest;
import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.mapper.ZgradaMapper;
import com.efacility.ticketing.model.Zgrada;
import com.efacility.ticketing.repository.StanRepository;
import com.efacility.ticketing.repository.ZgradaRepository;
import com.efacility.ticketing.service.ZgradaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ZgradaServiceImpl implements ZgradaService {

    private final ZgradaRepository buildingRepository;
    private final StanRepository apartmentRepository;
    private final ZgradaMapper buildingMapper;

    public ZgradaServiceImpl(ZgradaRepository buildingRepository,
                           StanRepository apartmentRepository,
                           ZgradaMapper buildingMapper) {
        this.buildingRepository = buildingRepository;
        this.apartmentRepository = apartmentRepository;
        this.buildingMapper = buildingMapper;
    }

    @Transactional(readOnly = true)
    public List<ZgradaDTO> getAll() {
        return buildingRepository.findAll()
                .stream()
                .map(buildingMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    // Server-side paginacija: klijent traži stranu (page) i veličinu (size),
    // baza vraća samo toliko redova + meta-podatke za navigaciju.
    @Transactional(readOnly = true)
    public Map<String, Object> getPaged(int page, int size) {
        Page<Zgrada> result = buildingRepository.findAllPaged(PageRequest.of(page, size));

        System.out.println("[PAGINACIJA] traženo: page=" + page + ", size=" + size
                + " -> baza vratila " + result.getNumberOfElements() + " zgrada"
                + " (ukupno u bazi: " + result.getTotalElements()
                + ", ukupno strana: " + result.getTotalPages() + ")");

        List<ZgradaDTO> content = result.getContent().stream()
                .map(buildingMapper::toDomainDTO)
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("buildings", content);
        data.put("page", result.getNumber());
        data.put("size", result.getSize());
        data.put("totalElements", result.getTotalElements());
        data.put("totalPages", result.getTotalPages());
        data.put("first", result.isFirst());
        data.put("last", result.isLast());
        return data;
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

        if (apartmentRepository.existsByBuildingId(id)) {
            throw new IllegalArgumentException(
                    "Zgrada se ne može obrisati dok postoje stanovi povezani sa njom.");
        }

        buildingRepository.delete(building);
        return "Zgrada je uspešno obrisana!";
    }
}
