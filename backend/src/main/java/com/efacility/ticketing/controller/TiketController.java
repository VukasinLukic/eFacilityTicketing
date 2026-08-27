package com.efacility.ticketing.controller;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import com.efacility.ticketing.dto.TiketDTO;
import com.efacility.ticketing.dto.request.AssignTiketRequest;
import com.efacility.ticketing.dto.request.CreateTiketRequest;
import com.efacility.ticketing.dto.request.UpdatePrioritetRequest;
import com.efacility.ticketing.dto.request.UpdateStatusRequest;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.enums.Prioritet;
import com.efacility.ticketing.model.enums.StatusTiketa;
import com.efacility.ticketing.service.TiketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tickets")
@CrossOrigin("http://localhost:3000")
public class TiketController {

    private final TiketService tiketService;

    public TiketController(TiketService tiketService) {
        this.tiketService = tiketService;
    }

    @PostMapping("/create")
    public ResponseEntity<Response> create(@Valid @RequestBody CreateTiketRequest request,
                                           @AuthenticationPrincipal Korisnik currentKorisnik) {
        TiketDTO ticket = tiketService.createTiket(request, currentKorisnik);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Ticket created", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAll(
            @RequestParam(required = false) StatusTiketa status,
            @RequestParam(required = false) Prioritet priority,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<TiketDTO> page = tiketService.getAllTikets(status, priority, buildingId, search, pageable);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Tickets fetched", Map.of(
                        "tickets", page.getContent(),
                        "totalElements", page.getTotalElements(),
                        "totalPages", page.getTotalPages(),
                        "currentPage", page.getNumber(),
                        "pageSize", page.getSize()
                ), HttpStatus.OK)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<Response> getMyTikets(@AuthenticationPrincipal Korisnik currentKorisnik) {
        List<TiketDTO> tickets = tiketService.getMyTikets(currentKorisnik.getId());
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Tickets fetched", Map.of("tickets", tickets), HttpStatus.OK)
        );
    }

    @GetMapping("/assigned")
    public ResponseEntity<Response> getAssigned(@AuthenticationPrincipal Korisnik currentKorisnik) {
        List<TiketDTO> tickets = tiketService.getAssignedTikets(currentKorisnik.getId());
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Tickets fetched", Map.of("tickets", tickets), HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getTiket(@PathVariable Long id,
                                              @AuthenticationPrincipal Korisnik currentKorisnik) {
        TiketDTO ticket = tiketService.getTiket(id, currentKorisnik);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Ticket fetched", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }

    @PutMapping("/assign")
    public ResponseEntity<Response> assign(@Valid @RequestBody AssignTiketRequest request,
                                           @AuthenticationPrincipal Korisnik currentKorisnik) {
        TiketDTO ticket = tiketService.assignTechnician(request, currentKorisnik);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Technician assigned successfully", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<Response> updateStatus(@Valid @RequestBody UpdateStatusRequest request,
                                                 @AuthenticationPrincipal Korisnik currentKorisnik) {
        TiketDTO ticket = tiketService.updateStatus(request, currentKorisnik);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Status updated successfully", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }

    @PutMapping("/updatePriority")
    public ResponseEntity<Response> updatePrioritet(@Valid @RequestBody UpdatePrioritetRequest request,
                                                   @AuthenticationPrincipal Korisnik currentKorisnik) {
        TiketDTO ticket = tiketService.updatePrioritet(request, currentKorisnik);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Priority updated successfully", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }
}
