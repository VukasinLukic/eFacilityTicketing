package com.efacility.ticketing.service;

import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.mapper.StanMapper;
import com.efacility.ticketing.repository.StanRepository;
import com.efacility.ticketing.repository.TiketRepository;
import com.efacility.ticketing.repository.ZgradaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StanServiceTest {

    @Mock
    private StanRepository apartmentRepository;
    @Mock
    private ZgradaRepository buildingRepository;
    @Mock
    private TiketRepository ticketRepository;
    @Mock
    private StanMapper apartmentMapper;

    private StanService stanService;

    @BeforeEach
    void setUp() {
        stanService = new StanService(apartmentRepository, buildingRepository,
                ticketRepository, apartmentMapper);
    }

    @Test
    void brisanjeStanaBezTiketaProlazi() {
        when(apartmentRepository.existsById(5L)).thenReturn(true);
        when(ticketRepository.existsByApartmentId(5L)).thenReturn(false);

        String result = stanService.deleteStan(5L);

        assertThat(result).isEqualTo("Stan je uspešno obrisan!");
        verify(apartmentRepository).deleteById(5L);
    }

    @Test
    void brisanjeStanaSaTiketimaBacaGresku() {
        when(apartmentRepository.existsById(5L)).thenReturn(true);
        when(ticketRepository.existsByApartmentId(5L)).thenReturn(true);

        assertThatThrownBy(() -> stanService.deleteStan(5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Stan se ne može obrisati: za njega postoje prijavljeni tiketi.");

        verify(apartmentRepository, never()).deleteById(any());
    }

    @Test
    void brisanjeNepostojecegStanaBacaResourceNotFound() {
        when(apartmentRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> stanService.deleteStan(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(apartmentRepository, never()).deleteById(any());
    }
}
