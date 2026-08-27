package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.TiketDTO;
import com.efacility.ticketing.dto.request.AssignTiketRequest;
import com.efacility.ticketing.dto.request.CreateTiketRequest;
import com.efacility.ticketing.dto.request.UpdatePrioritetRequest;
import com.efacility.ticketing.dto.request.UpdateStatusRequest;
import com.efacility.ticketing.exception.InvalidStatusTransitionException;
import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.exception.TiketAccessDeniedException;
import com.efacility.ticketing.mapper.TiketMapper;
import com.efacility.ticketing.model.Stan;
import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.enums.Uloga;
import com.efacility.ticketing.model.enums.StatusTiketa;
import com.efacility.ticketing.model.enums.Prioritet;
import com.efacility.ticketing.repository.StanRepository;
import com.efacility.ticketing.repository.TiketRepository;
import com.efacility.ticketing.repository.KorisnikRepository;
import com.efacility.ticketing.repository.spec.TiketSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TiketService {

    private final TiketRepository ticketRepository;
    private final StanRepository apartmentRepository;
    private final KorisnikRepository userRepository;
    private final TiketMapper ticketMapper;
    private final IstorijaTiketaService ticketHistoryService;

    public TiketService(TiketRepository ticketRepository,
                         StanRepository apartmentRepository,
                         KorisnikRepository userRepository,
                         TiketMapper ticketMapper,
                         IstorijaTiketaService ticketHistoryService) {
        this.ticketRepository = ticketRepository;
        this.apartmentRepository = apartmentRepository;
        this.userRepository = userRepository;
        this.ticketMapper = ticketMapper;
        this.ticketHistoryService = ticketHistoryService;
    }

    public TiketDTO createTiket(CreateTiketRequest request, Korisnik currentKorisnik) {
        Stan apartment = apartmentRepository.findById(request.getApartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + request.getApartmentId()));

        Korisnik tenant = userRepository.findById(currentKorisnik.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Tiket ticket = new Tiket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(StatusTiketa.OPEN);
        ticket.setTenant(tenant);
        ticket.setApartment(apartment);

        Tiket saved = ticketRepository.save(ticket);
        ticketHistoryService.createHistoryEntry(saved, tenant, null, StatusTiketa.OPEN);

        return ticketMapper.toDomainDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<TiketDTO> getAllTikets(StatusTiketa status, Prioritet priority, Long buildingId,
                                        String search, Pageable pageable) {
        return ticketRepository
                .findAll(TiketSpecifications.withFilters(status, priority, buildingId, search), pageable)
                .map(ticketMapper::toDomainDTO);
    }

    @Transactional(readOnly = true)
    public List<TiketDTO> getMyTikets(Long tenantId) {
        return ticketRepository.findByTenantId(tenantId)
                .stream()
                .map(ticketMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TiketDTO> getAssignedTikets(Long technicianId) {
        return ticketRepository.findByTechnicianId(technicianId)
                .stream()
                .map(ticketMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TiketDTO getTiket(Long ticketId, Korisnik currentKorisnik) {
        Tiket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        checkTiketAccess(ticket, currentKorisnik);
        return ticketMapper.toDomainDTO(ticket);
    }

    public TiketDTO assignTechnician(AssignTiketRequest request, Korisnik manager) {
        Tiket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + request.getTicketId()));

        if (ticket.getStatus() != StatusTiketa.OPEN) {
            throw new InvalidStatusTransitionException(
                    "Can only assign technician to OPEN tickets. Current status: " + ticket.getStatus());
        }

        Korisnik technician = userRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getTechnicianId()));

        if (technician.getRole() != Uloga.TECHNICIAN) {
            throw new IllegalArgumentException("User with id " + request.getTechnicianId() + " is not a TECHNICIAN");
        }

        Korisnik managedManager = userRepository.findById(manager.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StatusTiketa oldStatus = ticket.getStatus();
        ticket.setTechnician(technician);
        ticket.setManager(managedManager);
        ticket.setStatus(StatusTiketa.ASSIGNED);

        Tiket saved = ticketRepository.save(ticket);
        ticketHistoryService.createHistoryEntry(saved, managedManager, oldStatus, StatusTiketa.ASSIGNED);

        return ticketMapper.toDomainDTO(saved);
    }

    public TiketDTO updateStatus(UpdateStatusRequest request, Korisnik currentKorisnik) {
        Tiket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + request.getTicketId()));

        Korisnik managedKorisnik = userRepository.findById(currentKorisnik.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StatusTiketa oldStatus = ticket.getStatus();
        StatusTiketa newStatus = request.getNewStatus();

        validateStatusTransition(ticket, oldStatus, newStatus, managedKorisnik);

        ticket.setStatus(newStatus);
        Tiket saved = ticketRepository.save(ticket);
        ticketHistoryService.createHistoryEntry(saved, managedKorisnik, oldStatus, newStatus);

        return ticketMapper.toDomainDTO(saved);
    }

    public TiketDTO updatePrioritet(UpdatePrioritetRequest request, Korisnik currentKorisnik) {
        Tiket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + request.getTicketId()));

        if (ticket.getStatus() == StatusTiketa.CLOSED) {
            throw new InvalidStatusTransitionException(
                    "Cannot change priority of a CLOSED ticket.");
        }

        ticket.setPriority(request.getPriority());
        Tiket saved = ticketRepository.save(ticket);

        return ticketMapper.toDomainDTO(saved);
    }

    // ===================== Helpers =====================

    private void checkTiketAccess(Tiket ticket, Korisnik currentKorisnik) {
        switch (currentKorisnik.getRole()) {
            case MANAGER -> {
                // Manager sees all tickets
            }
            case TENANT -> {
                if (!ticket.getTenant().getId().equals(currentKorisnik.getId())) {
                    throw new TiketAccessDeniedException("You can only view your own tickets");
                }
            }
            case TECHNICIAN -> {
                if (ticket.getTechnician() == null || !ticket.getTechnician().getId().equals(currentKorisnik.getId())) {
                    throw new TiketAccessDeniedException("You can only view tickets assigned to you");
                }
            }
        }
    }

    private void validateStatusTransition(Tiket ticket, StatusTiketa current, StatusTiketa next, Korisnik user) {
        Uloga role = user.getRole();

        if (role == Uloga.TECHNICIAN) {
            if (ticket.getTechnician() == null || !ticket.getTechnician().getId().equals(user.getId())) {
                throw new TiketAccessDeniedException("You are not the assigned technician for this ticket");
            }
            boolean valid = (current == StatusTiketa.ASSIGNED && next == StatusTiketa.IN_PROGRESS)
                    || (current == StatusTiketa.IN_PROGRESS && next == StatusTiketa.COMPLETED);
            if (!valid) {
                throw new InvalidStatusTransitionException(
                        "Technician can only change: ASSIGNED → IN_PROGRESS or IN_PROGRESS → COMPLETED. " +
                        "Current: " + current + ", Requested: " + next);
            }
            return;
        }

        if (role == Uloga.MANAGER) {
            if (current != StatusTiketa.COMPLETED || next != StatusTiketa.CLOSED) {
                throw new InvalidStatusTransitionException(
                        "Manager can only close a COMPLETED ticket. Current: " + current
                                + ", Requested: " + next);
            }
        }
    }
}
