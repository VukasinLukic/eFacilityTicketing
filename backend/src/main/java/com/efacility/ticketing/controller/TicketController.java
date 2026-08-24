package com.efacility.ticketing.controller;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import com.efacility.ticketing.dto.TicketDTO;
import com.efacility.ticketing.dto.request.AssignTicketRequest;
import com.efacility.ticketing.dto.request.CreateTicketRequest;
import com.efacility.ticketing.dto.request.UpdatePriorityRequest;
import com.efacility.ticketing.dto.request.UpdateStatusRequest;
import com.efacility.ticketing.model.User;
import com.efacility.ticketing.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tickets")
@CrossOrigin("http://localhost:3000")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/create")
    public ResponseEntity<Response> create(@Valid @RequestBody CreateTicketRequest request,
                                           @AuthenticationPrincipal User currentUser) {
        TicketDTO ticket = ticketService.createTicket(request, currentUser);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Ticket created", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAll() {
        List<TicketDTO> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Tickets fetched", Map.of("tickets", tickets), HttpStatus.OK)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<Response> getMyTickets(@AuthenticationPrincipal User currentUser) {
        List<TicketDTO> tickets = ticketService.getMyTickets(currentUser.getId());
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Tickets fetched", Map.of("tickets", tickets), HttpStatus.OK)
        );
    }

    @GetMapping("/assigned")
    public ResponseEntity<Response> getAssigned(@AuthenticationPrincipal User currentUser) {
        List<TicketDTO> tickets = ticketService.getAssignedTickets(currentUser.getId());
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Tickets fetched", Map.of("tickets", tickets), HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getTicket(@PathVariable Long id,
                                              @AuthenticationPrincipal User currentUser) {
        TicketDTO ticket = ticketService.getTicket(id, currentUser);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Ticket fetched", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }

    @PostMapping("/assign")
    public ResponseEntity<Response> assign(@Valid @RequestBody AssignTicketRequest request,
                                           @AuthenticationPrincipal User currentUser) {
        TicketDTO ticket = ticketService.assignTechnician(request, currentUser);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Technician assigned successfully", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }

    @PostMapping("/updateStatus")
    public ResponseEntity<Response> updateStatus(@Valid @RequestBody UpdateStatusRequest request,
                                                 @AuthenticationPrincipal User currentUser) {
        TicketDTO ticket = ticketService.updateStatus(request, currentUser);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Status updated successfully", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }

    @PostMapping("/updatePriority")
    public ResponseEntity<Response> updatePriority(@Valid @RequestBody UpdatePriorityRequest request,
                                                   @AuthenticationPrincipal User currentUser) {
        TicketDTO ticket = ticketService.updatePriority(request, currentUser);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Priority updated successfully", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }
}
