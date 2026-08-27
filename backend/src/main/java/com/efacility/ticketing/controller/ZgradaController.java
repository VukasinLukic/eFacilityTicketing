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
@CrossOrigin("http://localhost:3000")
public class ZgradaController {

    private final ZgradaService zgradaService;

    public ZgradaController(ZgradaService zgradaService) {
        this.zgradaService = zgradaService;
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAll() {
        List<ZgradaDTO> buildings = zgradaService.getAll();
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Buildings fetched", Map.of("buildings", buildings), HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getZgrada(@PathVariable Long id) {
        ZgradaDTO building = zgradaService.getZgrada(id);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Building fetched", Map.of("building", building), HttpStatus.OK)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<Response> add(@Valid @RequestBody CreateZgradaRequest request) {
        ZgradaDTO building = zgradaService.addZgrada(request);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Building added successfully", Map.of("building", building), HttpStatus.OK)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<Response> update(@Valid @RequestBody UpdateZgradaRequest request) {
        ZgradaDTO building = zgradaService.updateZgrada(request);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Building updated successfully", Map.of("building", building), HttpStatus.OK)
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
