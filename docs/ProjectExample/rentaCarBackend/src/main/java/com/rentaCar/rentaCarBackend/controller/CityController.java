package com.rentaCar.rentaCarBackend.controller;

import com.rentaCar.rentaCarBackend.connection.HttpResponse;
import com.rentaCar.rentaCarBackend.connection.Response;
import com.rentaCar.rentaCarBackend.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/cities") // IZMENA
public class CityController {

    private final CityService cityService; // IZMENA

    public CityController(CityService cityService) { // IZMENA
        this.cityService = cityService;
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData(
                        "Successfully found all cities!", // IZMENA
                        Map.of("values", cityService.getAll()),
                        HttpStatus.OK));
    }
}