package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.ZgradaDTO;
import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.mapper.ZgradaMapper;
import com.efacility.ticketing.model.Stan;
import com.efacility.ticketing.model.Zgrada;
import com.efacility.ticketing.repository.TiketRepository;
import com.efacility.ticketing.repository.ZgradaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    private TiketRepository ticketRepository;
    @Mock
    private ZgradaMapper buildingMapper;

    private ZgradaService zgradaService;

    private Zgrada building;

    @BeforeEach
    void setUp() {
        zgradaService = new ZgradaService(buildingRepository, ticketRepository, buildingMapper);

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
        when(ticketRepository.existsByApartment_Building_Id(10L)).thenReturn(false);

        String result = zgradaService.deleteZgrada(10L);

        assertThat(result).isEqualTo("Zgrada je uspešno obrisana!");
        verify(buildingRepository).delete(building);
    }

    @Test
    void deletesBuildingWithApartmentsButNoTicketsAndCascadesToApartments() {
        building.getApartments().addAll(List.of(apartment(1L, "4B"), apartment(2L, "5A")));

        when(buildingRepository.findById(10L)).thenReturn(Optional.of(building));
        when(ticketRepository.existsByApartment_Building_Id(10L)).thenReturn(false);

        String result = zgradaService.deleteZgrada(10L);

        assertThat(result).isEqualTo("Zgrada je uspešno obrisana!");

        ArgumentCaptor<Zgrada> deleted = ArgumentCaptor.forClass(Zgrada.class);
        verify(buildingRepository).delete(deleted.capture());
        assertThat(deleted.getValue().getApartments())
                .extracting(Stan::getNumber)
                .containsExactly("4B", "5A");
        verify(buildingRepository, never()).deleteById(any());
    }

    @Test
    void refusesToDeleteBuildingWhenAnApartmentHasTickets() {
        building.getApartments().add(apartment(1L, "4B"));

        when(buildingRepository.findById(10L)).thenReturn(Optional.of(building));
        when(ticketRepository.existsByApartment_Building_Id(10L)).thenReturn(true);

        assertThatThrownBy(() -> zgradaService.deleteZgrada(10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Zgrada se ne može obrisati: neki stanovi imaju vezane tikete.");

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
