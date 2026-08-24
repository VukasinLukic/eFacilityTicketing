package com.efacility.ticketing.controller;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import com.efacility.ticketing.dto.ApartmentDTO;
import com.efacility.ticketing.dto.request.CreateApartmentRequest;
import com.efacility.ticketing.dto.request.UpdateApartmentRequest;
import com.efacility.ticketing.service.ApartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apartments")
@CrossOrigin("http://localhost:3000")
public class ApartmentController {

    private final ApartmentService apartmentService;

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAll() {
        List<ApartmentDTO> apartments = apartmentService.getAll();
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Apartments fetched", Map.of("apartments", apartments), HttpStatus.OK)
        );
    }

    @GetMapping("/byBuilding/{buildingId}")
    public ResponseEntity<Response> getByBuilding(@PathVariable Long buildingId) {
        List<ApartmentDTO> apartments = apartmentService.getByBuilding(buildingId);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Apartments fetched", Map.of("apartments", apartments), HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getApartment(@PathVariable Long id) {
        ApartmentDTO apartment = apartmentService.getApartment(id);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Apartment fetched", Map.of("apartment", apartment), HttpStatus.OK)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<Response> add(@Valid @RequestBody CreateApartmentRequest request) {
        ApartmentDTO apartment = apartmentService.addApartment(request);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Apartment added successfully", Map.of("apartment", apartment), HttpStatus.OK)
        );
    }

    @PostMapping("/update")
    public ResponseEntity<Response> update(@Valid @RequestBody UpdateApartmentRequest request) {
        ApartmentDTO apartment = apartmentService.updateApartment(request);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Apartment updated successfully", Map.of("apartment", apartment), HttpStatus.OK)
        );
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        String result = apartmentService.deleteApartment(id);
        return ResponseEntity.ok(
                HttpResponse.getResponse(result, HttpStatus.OK)
        );
    }
}
