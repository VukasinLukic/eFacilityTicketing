package com.efacility.ticketing.controller;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import com.efacility.ticketing.dto.IstorijaTiketaDTO;
import com.efacility.ticketing.service.IstorijaTiketaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ticket-history")
@CrossOrigin("http://localhost:3000")
public class IstorijaTiketaController {

    private final IstorijaTiketaService ticketHistoryService;

    public IstorijaTiketaController(IstorijaTiketaService ticketHistoryService) {
        this.ticketHistoryService = ticketHistoryService;
    }

    @GetMapping("/byTicket/{ticketId}")
    public ResponseEntity<Response> getByTiket(@PathVariable Long ticketId) {
        List<IstorijaTiketaDTO> history = ticketHistoryService.getHistoryByTiket(ticketId);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("History fetched", Map.of("history", history), HttpStatus.OK)
        );
    }
}
