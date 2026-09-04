package com.efacility.ticketing.service.impl;

import com.efacility.ticketing.dto.StanDTO;
import com.efacility.ticketing.dto.request.CreateStanRequest;
import com.efacility.ticketing.dto.request.UpdateStanRequest;
import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.mapper.StanMapper;
import com.efacility.ticketing.model.Stan;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.Zgrada;
import com.efacility.ticketing.model.enums.Uloga;
import com.efacility.ticketing.repository.KorisnikRepository;
import com.efacility.ticketing.repository.StanRepository;
import com.efacility.ticketing.repository.TiketRepository;
import com.efacility.ticketing.repository.ZgradaRepository;
import com.efacility.ticketing.service.StanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StanServiceImpl implements StanService {

    private final StanRepository apartmentRepository;
    private final ZgradaRepository buildingRepository;
    private final TiketRepository ticketRepository;
    private final KorisnikRepository userRepository;
    private final StanMapper apartmentMapper;

    public StanServiceImpl(StanRepository apartmentRepository,
                            ZgradaRepository buildingRepository,
                            TiketRepository ticketRepository,
                            KorisnikRepository userRepository,
                            StanMapper apartmentMapper) {
        this.apartmentRepository = apartmentRepository;
        this.buildingRepository = buildingRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
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
    public List<StanDTO> getMy(Long tenantId) {
        return apartmentRepository.findByTenantId(tenantId)
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
        apartment.setTenant(resolveTenant(request.getTenantId()));
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
        apartment.setTenant(resolveTenant(request.getTenantId()));
        Stan saved = apartmentRepository.save(apartment);
        return apartmentMapper.toDomainDTO(saved);
    }

    public StanDTO assignTenant(Long apartmentId, Long tenantId) {
        Stan apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Stan nije pronađen, id: " + apartmentId));

        apartment.setTenant(resolveTenant(tenantId));

        return apartmentMapper.toDomainDTO(apartmentRepository.save(apartment));
    }

    private Korisnik resolveTenant(Long tenantId) {
        if (tenantId == null) {
            return null;
        }
        Korisnik tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronađen, id: " + tenantId));
        if (tenant.getRole() != Uloga.TENANT) {
            throw new IllegalArgumentException("Stanu se može dodeliti samo korisnik sa ulogom TENANT.");
        }
        return tenant;
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
