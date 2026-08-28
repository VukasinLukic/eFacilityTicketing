package com.efacility.ticketing.service;

import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.exception.TiketAccessDeniedException;
import com.efacility.ticketing.mapper.IstorijaTiketaMapper;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.enums.Uloga;
import com.efacility.ticketing.repository.IstorijaTiketaRepository;
import com.efacility.ticketing.repository.TiketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IstorijaTiketaServiceTest {

    @Mock
    private IstorijaTiketaRepository ticketHistoryRepository;
    @Mock
    private TiketRepository ticketRepository;
    @Mock
    private IstorijaTiketaMapper ticketHistoryMapper;

    private IstorijaTiketaService service;

    private Korisnik vlasnik;
    private Korisnik drugiStanar;
    private Korisnik dodeljeniTehnicar;
    private Korisnik menadzer;
    private Tiket ticket;

    @BeforeEach
    void setUp() {
        service = new IstorijaTiketaService(ticketHistoryRepository, ticketRepository,
                ticketHistoryMapper, new TiketPristup());

        vlasnik = korisnik(1L, Uloga.TENANT);
        drugiStanar = korisnik(2L, Uloga.TENANT);
        dodeljeniTehnicar = korisnik(3L, Uloga.TECHNICIAN);
        menadzer = korisnik(4L, Uloga.MANAGER);

        ticket = new Tiket();
        ticket.setId(100L);
        ticket.setTenant(vlasnik);
        ticket.setTechnician(dodeljeniTehnicar);

        lenient().when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));
        lenient().when(ticketHistoryRepository.findByTicketIdOrderByChangedAtAsc(100L))
                .thenReturn(List.of());
    }

    private Korisnik korisnik(Long id, Uloga uloga) {
        Korisnik k = new Korisnik();
        k.setId(id);
        k.setRole(uloga);
        return k;
    }

    @Test
    void stanarVidiIstorijuSvogTiketa() {
        assertThatCode(() -> service.getHistoryByTiket(100L, vlasnik)).doesNotThrowAnyException();
    }

    @Test
    void stanarNeVidiIstorijuTudjegTiketa() {
        assertThatThrownBy(() -> service.getHistoryByTiket(100L, drugiStanar))
                .isInstanceOf(TiketAccessDeniedException.class);
    }

    @Test
    void tehnicarNeVidiIstorijuTiketaKojiMuNijeDodeljen() {
        Korisnik drugiTehnicar = korisnik(9L, Uloga.TECHNICIAN);

        assertThatThrownBy(() -> service.getHistoryByTiket(100L, drugiTehnicar))
                .isInstanceOf(TiketAccessDeniedException.class);
    }

    @Test
    void dodeljeniTehnicarVidiIstoriju() {
        assertThatCode(() -> service.getHistoryByTiket(100L, dodeljeniTehnicar))
                .doesNotThrowAnyException();
    }

    @Test
    void menadzerVidiIstorijuSvakogTiketa() {
        assertThatCode(() -> service.getHistoryByTiket(100L, menadzer)).doesNotThrowAnyException();
    }

    @Test
    void nepostojeciTiketBacaResourceNotFound() {
        when(ticketRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getHistoryByTiket(404L, menadzer))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
