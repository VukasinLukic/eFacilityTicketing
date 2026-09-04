package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.ZgradaDTO;
import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.mapper.ZgradaMapper;
import com.efacility.ticketing.model.Stan;
import com.efacility.ticketing.model.Zgrada;
import com.efacility.ticketing.repository.StanRepository;
import com.efacility.ticketing.repository.ZgradaRepository;
import com.efacility.ticketing.service.impl.ZgradaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZgradaServiceTest {

    @Mock
    private ZgradaRepository buildingRepository;
    @Mock
    private StanRepository apartmentRepository;
    @Mock
    private ZgradaMapper buildingMapper;

    private ZgradaService zgradaService;

    private Zgrada building;

    @BeforeEach
    void setUp() {
        zgradaService = new ZgradaServiceImpl(buildingRepository, apartmentRepository, buildingMapper);

        building = new Zgrada();
        building.setId(10L);
        building.setName("Sunrise Tower");
        building.setAddress("Bulevar kralja Aleksandra 73");
    }

    private Stan apartment(Long id, String number) {
        Stan stan = new Stan();
        stan.setId(id);
        stan.setNumber(number);
        stan.setFloor(3);
        stan.setBuilding(building);
        return stan;
    }

    @Test
    void deletesBuildingWithoutApartments() {
        when(buildingRepository.findById(10L)).thenReturn(Optional.of(building));
        when(apartmentRepository.existsByBuildingId(10L)).thenReturn(false);

        String result = zgradaService.deleteZgrada(10L);

        assertThat(result).isEqualTo("Zgrada je uspešno obrisana!");
        verify(buildingRepository).delete(building);
    }

    @Test
    void refusesToDeleteBuildingWhileApartmentsExist() {
        building.getApartments().add(apartment(1L, "4B"));

        when(buildingRepository.findById(10L)).thenReturn(Optional.of(building));
        when(apartmentRepository.existsByBuildingId(10L)).thenReturn(true);

        assertThatThrownBy(() -> zgradaService.deleteZgrada(10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Zgrada se ne može obrisati dok postoje stanovi povezani sa njom.");

        verify(buildingRepository, never()).delete(any(Zgrada.class));
        verify(buildingRepository, never()).deleteById(any());
    }

    @Test
    void throwsWhenBuildingDoesNotExist() {
        when(buildingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> zgradaService.deleteZgrada(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(buildingRepository, never()).delete(any(Zgrada.class));
    }

    @Test
    void addBuildingReturnsMappedDto() {
        ZgradaDTO dto = new ZgradaDTO();
        dto.setId(10L);
        dto.setName("Sunrise Tower");

        when(buildingRepository.save(any(Zgrada.class))).thenAnswer(inv -> inv.getArgument(0));
        when(buildingMapper.toDomainDTO(any(Zgrada.class))).thenReturn(dto);

        com.efacility.ticketing.dto.request.CreateZgradaRequest request =
                new com.efacility.ticketing.dto.request.CreateZgradaRequest();
        request.setName("Sunrise Tower");
        request.setAddress("Bulevar kralja Aleksandra 73");

        ZgradaDTO result = zgradaService.addZgrada(request);

        assertThat(result.getName()).isEqualTo("Sunrise Tower");
    }
}
