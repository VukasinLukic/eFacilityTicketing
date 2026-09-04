package com.efacility.ticketing.service;

import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.mapper.StanMapper;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.Stan;
import com.efacility.ticketing.model.enums.Uloga;
import com.efacility.ticketing.repository.KorisnikRepository;
import com.efacility.ticketing.repository.StanRepository;
import com.efacility.ticketing.repository.TiketRepository;
import com.efacility.ticketing.repository.ZgradaRepository;
import com.efacility.ticketing.service.impl.StanServiceImpl;
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

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class StanServiceTest {

    @Mock
    private StanRepository apartmentRepository;
    @Mock
    private ZgradaRepository buildingRepository;
    @Mock
    private TiketRepository ticketRepository;
    @Mock
    private KorisnikRepository userRepository;
    @Mock
    private StanMapper apartmentMapper;

    private StanService stanService;

    @BeforeEach
    void setUp() {
        stanService = new StanServiceImpl(apartmentRepository, buildingRepository,
                ticketRepository, userRepository, apartmentMapper);
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

    @Test
    void menadzerMozeDodelitiStanStanarU() {
        Stan apartment = new Stan();
        apartment.setId(5L);
        Korisnik tenant = new Korisnik();
        tenant.setId(7L);
        tenant.setRole(Uloga.TENANT);

        when(apartmentRepository.findById(5L)).thenReturn(Optional.of(apartment));
        when(userRepository.findById(7L)).thenReturn(Optional.of(tenant));
        when(apartmentRepository.save(apartment)).thenReturn(apartment);

        stanService.assignTenant(5L, 7L);

        assertThat(apartment.getTenant()).isSameAs(tenant);
        verify(apartmentRepository).save(apartment);
    }

    @Test
    void tehnicarNeMozeBitiDodeljenStanuKaoStanar() {
        Stan apartment = new Stan();
        Korisnik technician = new Korisnik();
        technician.setRole(Uloga.TECHNICIAN);

        when(apartmentRepository.findById(5L)).thenReturn(Optional.of(apartment));
        when(userRepository.findById(8L)).thenReturn(Optional.of(technician));

        assertThatThrownBy(() -> stanService.assignTenant(5L, 8L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Stanu se može dodeliti samo korisnik sa ulogom TENANT.");

        verify(apartmentRepository, never()).save(any());
    }

    @Test
    void uklanjanjeStanaraOstavljaStanSlobodnim() {
        Stan apartment = new Stan();
        apartment.setTenant(new Korisnik());

        when(apartmentRepository.findById(5L)).thenReturn(Optional.of(apartment));
        when(apartmentRepository.save(apartment)).thenReturn(apartment);

        stanService.assignTenant(5L, null);

        assertThat(apartment.getTenant()).isNull();
    }

    @Test
    void stanarDobijaSamoSvojeStanove() {
        when(apartmentRepository.findByTenantId(7L)).thenReturn(List.of(new Stan(), new Stan()));

        stanService.getMy(7L);

        verify(apartmentRepository).findByTenantId(7L);
        verify(apartmentMapper, org.mockito.Mockito.times(2)).toDomainDTO(any(Stan.class));
    }
}
