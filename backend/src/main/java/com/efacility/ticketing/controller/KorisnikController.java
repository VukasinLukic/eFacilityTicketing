package com.efacility.ticketing.controller;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import com.efacility.ticketing.dto.KorisnikDTO;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.service.KorisnikService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin("http://localhost:3000")
public class KorisnikController {

    private final KorisnikService korisnikService;

    public KorisnikController(KorisnikService korisnikService) {
        this.korisnikService = korisnikService;
    }

    @GetMapping("/me")
    public ResponseEntity<Response> getMe(@AuthenticationPrincipal Korisnik currentKorisnik) {
        KorisnikDTO user = korisnikService.getCurrentKorisnik(currentKorisnik);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("User fetched", Map.of("user", user), HttpStatus.OK)
        );
    }

    @GetMapping("/technicians")
    public ResponseEntity<Response> getTechnicians() {
        List<KorisnikDTO> technicians = korisnikService.getAllTechnicians();
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Technicians fetched", Map.of("technicians", technicians), HttpStatus.OK)
        );
    }
}
