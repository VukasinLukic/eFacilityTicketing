package com.efacility.ticketing.controller;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import com.efacility.ticketing.dto.TicketHistoryDTO;
import com.efacility.ticketing.service.TicketHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ticket-history")
@CrossOrigin("http://localhost:3000")
public class TicketHistoryController {

    private final TicketHistoryService ticketHistoryService;

    public TicketHistoryController(TicketHistoryService ticketHistoryService) {
        this.ticketHistoryService = ticketHistoryService;
    }

    @GetMapping("/byTicket/{ticketId}")
    public ResponseEntity<Response> getByTicket(@PathVariable Long ticketId) {
        List<TicketHistoryDTO> history = ticketHistoryService.getHistoryByTicket(ticketId);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("History fetched", Map.of("history", history), HttpStatus.OK)
        );
    }
}
