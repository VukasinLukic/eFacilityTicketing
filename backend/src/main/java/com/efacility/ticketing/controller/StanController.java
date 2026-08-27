package com.efacility.ticketing.controller;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import com.efacility.ticketing.dto.StanDTO;
import com.efacility.ticketing.dto.request.CreateStanRequest;
import com.efacility.ticketing.dto.request.UpdateStanRequest;
import com.efacility.ticketing.service.StanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apartments")
@CrossOrigin("http://localhost:3000")
public class StanController {

    private final StanService apartmentService;

    public StanController(StanService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAll() {
        List<StanDTO> apartments = apartmentService.getAll();
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Apartments fetched", Map.of("apartments", apartments), HttpStatus.OK)
        );
    }

    @GetMapping("/byBuilding/{buildingId}")
    public ResponseEntity<Response> getByZgrada(@PathVariable Long buildingId) {
        List<StanDTO> apartments = apartmentService.getByZgrada(buildingId);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Apartments fetched", Map.of("apartments", apartments), HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getStan(@PathVariable Long id) {
        StanDTO apartment = apartmentService.getStan(id);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Apartment fetched", Map.of("apartment", apartment), HttpStatus.OK)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<Response> add(@Valid @RequestBody CreateStanRequest request) {
        StanDTO apartment = apartmentService.addStan(request);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Apartment added successfully", Map.of("apartment", apartment), HttpStatus.OK)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<Response> update(@Valid @RequestBody UpdateStanRequest request) {
        StanDTO apartment = apartmentService.updateStan(request);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Apartment updated successfully", Map.of("apartment", apartment), HttpStatus.OK)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        String result = apartmentService.deleteStan(id);
        return ResponseEntity.ok(
                HttpResponse.getResponse(result, HttpStatus.OK)
        );
    }
}
