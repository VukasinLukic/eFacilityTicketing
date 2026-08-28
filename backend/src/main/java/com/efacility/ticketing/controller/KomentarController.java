package com.efacility.ticketing.controller;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import com.efacility.ticketing.dto.KomentarDTO;
import com.efacility.ticketing.dto.request.AddKomentarRequest;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.service.KomentarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class KomentarController {

    private final KomentarService komentarService;

    public KomentarController(KomentarService komentarService) {
        this.komentarService = komentarService;
    }

    @GetMapping("/byTicket/{ticketId}")
    public ResponseEntity<Response> getByTiket(@PathVariable Long ticketId,
                                                @AuthenticationPrincipal Korisnik currentKorisnik) {
        List<KomentarDTO> comments = komentarService.getKomentarsByTiket(ticketId, currentKorisnik);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Komentari su učitani.", Map.of("comments", comments), HttpStatus.OK)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<Response> add(@Valid @RequestBody AddKomentarRequest request,
                                        @AuthenticationPrincipal Korisnik currentKorisnik) {
        KomentarDTO comment = komentarService.addKomentar(request, currentKorisnik);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Komentar je dodat!", Map.of("comment", comment), HttpStatus.OK)
        );
    }
}
