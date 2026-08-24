package com.rentaCar.rentaCarBackend.controller;

import com.rentaCar.rentaCarBackend.connection.HttpResponse;
import com.rentaCar.rentaCarBackend.connection.Response;
import com.rentaCar.rentaCarBackend.service.CarModelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin("http://localhost:4200")
@RequestMapping("/car-models") // IZMENA
public class CarModelController {

    private final CarModelService carModelService; // IZMENA

    public CarModelController(CarModelService carModelService) { // IZMENA
        this.carModelService = carModelService;
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData(
                        "Car models have been found!", // IZMENA
                        Map.of("values", carModelService.getAll()),
                        HttpStatus.OK));
    }
}