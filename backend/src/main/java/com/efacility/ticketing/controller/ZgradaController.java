package com.efacility.ticketing.controller;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import com.efacility.ticketing.dto.ZgradaDTO;
import com.efacility.ticketing.dto.request.CreateZgradaRequest;
import com.efacility.ticketing.dto.request.UpdateZgradaRequest;
import com.efacility.ticketing.service.ZgradaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/buildings")
public class ZgradaController {

    private final ZgradaService zgradaService;

    public ZgradaController(ZgradaService zgradaService) {
        this.zgradaService = zgradaService;
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAll() {
        List<ZgradaDTO> buildings = zgradaService.getAll();
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Zgrade su učitane.", Map.of("buildings", buildings), HttpStatus.OK)
        );
    }

    // Server-side paginacija: GET /buildings/paged?page=0&size=5
    @GetMapping("/paged")
    public ResponseEntity<Response> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        System.out.println("[ZgradaController] GET /buildings/paged -> page=" + page + ", size=" + size);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Zgrade su učitane (stranica " + page + ").",
                        zgradaService.getPaged(page, size), HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getZgrada(@PathVariable Long id) {
        ZgradaDTO building = zgradaService.getZgrada(id);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Zgrada je učitana.", Map.of("building", building), HttpStatus.OK)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<Response> add(@Valid @RequestBody CreateZgradaRequest request) {
        ZgradaDTO building = zgradaService.addZgrada(request);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Zgrada je uspešno dodata!", Map.of("building", building), HttpStatus.OK)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<Response> update(@Valid @RequestBody UpdateZgradaRequest request) {
        ZgradaDTO building = zgradaService.updateZgrada(request);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Zgrada je uspešno ažurirana!", Map.of("building", building), HttpStatus.OK)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        String result = zgradaService.deleteZgrada(id);
        return ResponseEntity.ok(
                HttpResponse.getResponse(result, HttpStatus.OK)
        );
    }
}
