package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.TiketDTO;
import com.efacility.ticketing.dto.request.CreateTiketRequest;
import com.efacility.ticketing.dto.request.UpdatePrioritetRequest;
import com.efacility.ticketing.dto.request.UpdateStatusRequest;
import com.efacility.ticketing.exception.InvalidStatusTransitionException;
import com.efacility.ticketing.exception.TiketAccessDeniedException;
import com.efacility.ticketing.mapper.TiketMapper;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.Stan;
import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.enums.Prioritet;
import com.efacility.ticketing.model.enums.StatusTiketa;
import com.efacility.ticketing.model.enums.Uloga;
import com.efacility.ticketing.repository.KorisnikRepository;
import com.efacility.ticketing.repository.StanRepository;
import com.efacility.ticketing.repository.TiketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TiketServiceTest {

    @Mock
    private TiketRepository ticketRepository;
    @Mock
    private StanRepository apartmentRepository;
    @Mock
    private KorisnikRepository userRepository;
    @Mock
    private TiketMapper ticketMapper;
    @Mock
    private IstorijaTiketaService ticketHistoryService;
    @Mock
    private EmailService emailService;

    private TiketService tiketService;

    private Korisnik technician;
    private Korisnik manager;
    private Tiket ticket;

    @BeforeEach
    void setUp() {
        tiketService = new TiketService(ticketRepository, apartmentRepository, userRepository,
                ticketMapper, ticketHistoryService, new TiketPristup(), emailService);

        technician = new Korisnik();
        technician.setId(1L);
        technician.setRole(Uloga.TECHNICIAN);

        manager = new Korisnik();
        manager.setId(2L);
        manager.setRole(Uloga.MANAGER);

        ticket = new Tiket();
        ticket.setId(100L);
        ticket.setTechnician(technician);
        ticket.setPriority(Prioritet.MEDIUM);

        lenient().when(ticketRepository.save(any(Tiket.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(ticketMapper.toDomainDTO(any(Tiket.class))).thenReturn(new TiketDTO());
    }

    @Test
    void technicianCanMoveAssignedTicketToInProgress() {
        ticket.setStatus(StatusTiketa.ASSIGNED);
        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(1L)).thenReturn(Optional.of(technician));

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setTicketId(100L);
        request.setNewStatus(StatusTiketa.IN_PROGRESS);

        tiketService.updateStatus(request, technician);

        assertThat(ticket.getStatus()).isEqualTo(StatusTiketa.IN_PROGRESS);
    }

    @Test
    void technicianCannotSkipInProgressStep() {
        ticket.setStatus(StatusTiketa.ASSIGNED);
        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(1L)).thenReturn(Optional.of(technician));

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setTicketId(100L);
        request.setNewStatus(StatusTiketa.COMPLETED);

        assertThatThrownBy(() -> tiketService.updateStatus(request, technician))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void technicianCannotChangeStatusOfTicketNotAssignedToThem() {
        ticket.setStatus(StatusTiketa.ASSIGNED);
        Korisnik otherTechnician = new Korisnik();
        otherTechnician.setId(99L);
        otherTechnician.setRole(Uloga.TECHNICIAN);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(99L)).thenReturn(Optional.of(otherTechnician));

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setTicketId(100L);
        request.setNewStatus(StatusTiketa.IN_PROGRESS);

        assertThatThrownBy(() -> tiketService.updateStatus(request, otherTechnician))
                .isInstanceOf(TiketAccessDeniedException.class);
    }

    @Test
    void managerCanCloseACompletedTicket() {
        ticket.setStatus(StatusTiketa.COMPLETED);
        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(2L)).thenReturn(Optional.of(manager));

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setTicketId(100L);
        request.setNewStatus(StatusTiketa.CLOSED);

        tiketService.updateStatus(request, manager);

        assertThat(ticket.getStatus()).isEqualTo(StatusTiketa.CLOSED);
    }

    @Test
    void managerCannotReopenClosedTicket() {
        ticket.setStatus(StatusTiketa.CLOSED);
        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(2L)).thenReturn(Optional.of(manager));

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setTicketId(100L);
        request.setNewStatus(StatusTiketa.OPEN);

        assertThatThrownBy(() -> tiketService.updateStatus(request, manager))
                .isInstanceOf(InvalidStatusTransitionException.class);
        assertThat(ticket.getStatus()).isEqualTo(StatusTiketa.CLOSED);
    }

    @Test
    void cannotChangePriorityOfAClosedTicket() {
        ticket.setStatus(StatusTiketa.CLOSED);
        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));

        UpdatePrioritetRequest request = new UpdatePrioritetRequest();
        request.setTicketId(100L);
        request.setPriority(Prioritet.URGENT);

        assertThatThrownBy(() -> tiketService.updatePrioritet(request, manager))
                .isInstanceOf(InvalidStatusTransitionException.class);
        assertThat(ticket.getPriority()).isEqualTo(Prioritet.MEDIUM);
    }

    @Test
    void canChangePriorityOfAnOpenTicket() {
        ticket.setStatus(StatusTiketa.OPEN);
        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));

        UpdatePrioritetRequest request = new UpdatePrioritetRequest();
        request.setTicketId(100L);
        request.setPriority(Prioritet.URGENT);

        tiketService.updatePrioritet(request, manager);

        assertThat(ticket.getPriority()).isEqualTo(Prioritet.URGENT);
    }

    @Test
    void newlyCreatedTicketStartsWithoutHistoryEntries() {
        Korisnik tenant = new Korisnik();
        tenant.setId(3L);
        tenant.setRole(Uloga.TENANT);

        Stan apartment = new Stan();
        apartment.setId(4L);

        CreateTiketRequest request = new CreateTiketRequest();
        request.setTitle("Curi slavina");
        request.setDescription("Voda curi ispod sudopere.");
        request.setPriority(Prioritet.HIGH);
        request.setApartmentId(4L);

        when(apartmentRepository.findById(4L)).thenReturn(Optional.of(apartment));
        when(userRepository.findById(3L)).thenReturn(Optional.of(tenant));

        tiketService.createTiket(request, tenant);

        verify(ticketHistoryService, never()).createHistoryEntry(any(), any(), any(), any());
    }
}
