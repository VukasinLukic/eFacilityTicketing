package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.TicketDTO;
import com.efacility.ticketing.dto.request.AssignTicketRequest;
import com.efacility.ticketing.dto.request.CreateTicketRequest;
import com.efacility.ticketing.dto.request.UpdatePriorityRequest;
import com.efacility.ticketing.dto.request.UpdateStatusRequest;
import com.efacility.ticketing.exception.InvalidStatusTransitionException;
import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.exception.TicketAccessDeniedException;
import com.efacility.ticketing.mapper.TicketMapper;
import com.efacility.ticketing.model.Apartment;
import com.efacility.ticketing.model.Ticket;
import com.efacility.ticketing.model.User;
import com.efacility.ticketing.model.enums.Role;
import com.efacility.ticketing.model.enums.TicketStatus;
import com.efacility.ticketing.repository.ApartmentRepository;
import com.efacility.ticketing.repository.TicketRepository;
import com.efacility.ticketing.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;
    private final TicketHistoryService ticketHistoryService;

    public TicketService(TicketRepository ticketRepository,
                         ApartmentRepository apartmentRepository,
                         UserRepository userRepository,
                         TicketMapper ticketMapper,
                         TicketHistoryService ticketHistoryService) {
        this.ticketRepository = ticketRepository;
        this.apartmentRepository = apartmentRepository;
        this.userRepository = userRepository;
        this.ticketMapper = ticketMapper;
        this.ticketHistoryService = ticketHistoryService;
    }

    public TicketDTO createTicket(CreateTicketRequest request, User currentUser) {
        Apartment apartment = apartmentRepository.findById(request.getApartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + request.getApartmentId()));

        User tenant = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setTenant(tenant);
        ticket.setApartment(apartment);

        Ticket saved = ticketRepository.save(ticket);
        ticketHistoryService.createHistoryEntry(saved, tenant, null, TicketStatus.OPEN);

        return ticketMapper.toDomainDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(ticketMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TicketDTO> getMyTickets(Long tenantId) {
        return ticketRepository.findByTenantId(tenantId)
                .stream()
                .map(ticketMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TicketDTO> getAssignedTickets(Long technicianId) {
        return ticketRepository.findByTechnicianId(technicianId)
                .stream()
                .map(ticketMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketDTO getTicket(Long ticketId, User currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        checkTicketAccess(ticket, currentUser);
        return ticketMapper.toDomainDTO(ticket);
    }

    public TicketDTO assignTechnician(AssignTicketRequest request, User manager) {
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + request.getTicketId()));

        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new InvalidStatusTransitionException(
                    "Can only assign technician to OPEN tickets. Current status: " + ticket.getStatus());
        }

        User technician = userRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getTechnicianId()));

        if (technician.getRole() != Role.TECHNICIAN) {
            throw new IllegalArgumentException("User with id " + request.getTechnicianId() + " is not a TECHNICIAN");
        }

        User managedManager = userRepository.findById(manager.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TicketStatus oldStatus = ticket.getStatus();
        ticket.setTechnician(technician);
        ticket.setManager(managedManager);
        ticket.setStatus(TicketStatus.ASSIGNED);

        Ticket saved = ticketRepository.save(ticket);
        ticketHistoryService.createHistoryEntry(saved, managedManager, oldStatus, TicketStatus.ASSIGNED);

        return ticketMapper.toDomainDTO(saved);
    }

    public TicketDTO updateStatus(UpdateStatusRequest request, User currentUser) {
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + request.getTicketId()));

        User managedUser = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TicketStatus oldStatus = ticket.getStatus();
        TicketStatus newStatus = request.getNewStatus();

        validateStatusTransition(ticket, oldStatus, newStatus, managedUser);

        ticket.setStatus(newStatus);
        Ticket saved = ticketRepository.save(ticket);
        ticketHistoryService.createHistoryEntry(saved, managedUser, oldStatus, newStatus);

        return ticketMapper.toDomainDTO(saved);
    }

    public TicketDTO updatePriority(UpdatePriorityRequest request, User currentUser) {
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + request.getTicketId()));

        ticket.setPriority(request.getPriority());
        Ticket saved = ticketRepository.save(ticket);

        return ticketMapper.toDomainDTO(saved);
    }

    // ===================== Helpers =====================

    private void checkTicketAccess(Ticket ticket, User currentUser) {
        switch (currentUser.getRole()) {
            case MANAGER -> {
                // Manager sees all tickets
            }
            case TENANT -> {
                if (!ticket.getTenant().getId().equals(currentUser.getId())) {
                    throw new TicketAccessDeniedException("You can only view your own tickets");
                }
            }
            case TECHNICIAN -> {
                if (ticket.getTechnician() == null || !ticket.getTechnician().getId().equals(currentUser.getId())) {
                    throw new TicketAccessDeniedException("You can only view tickets assigned to you");
                }
            }
        }
    }

    private void validateStatusTransition(Ticket ticket, TicketStatus current, TicketStatus next, User user) {
        Role role = user.getRole();

        if (role == Role.TECHNICIAN) {
            if (ticket.getTechnician() == null || !ticket.getTechnician().getId().equals(user.getId())) {
                throw new TicketAccessDeniedException("You are not the assigned technician for this ticket");
            }
            boolean valid = (current == TicketStatus.ASSIGNED && next == TicketStatus.IN_PROGRESS)
                    || (current == TicketStatus.IN_PROGRESS && next == TicketStatus.COMPLETED);
            if (!valid) {
                throw new InvalidStatusTransitionException(
                        "Technician can only change: ASSIGNED → IN_PROGRESS or IN_PROGRESS → COMPLETED. " +
                        "Current: " + current + ", Requested: " + next);
            }
            return;
        }

        if (role == Role.MANAGER) {
            // Manager can re-open any ticket back to OPEN
            if (next == TicketStatus.OPEN) {
                return;
            }
            boolean valid = switch (current) {
                case ASSIGNED -> next == TicketStatus.IN_PROGRESS;
                case IN_PROGRESS -> next == TicketStatus.COMPLETED;
                case COMPLETED -> next == TicketStatus.CLOSED;
                default -> false;
            };
            if (!valid) {
                throw new InvalidStatusTransitionException(
                        "Invalid status transition from " + current + " to " + next);
            }
        }
    }
}
