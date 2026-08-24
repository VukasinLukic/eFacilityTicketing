package com.rentaCar.rentaCarBackend.controller;

import com.rentaCar.rentaCarBackend.connection.HttpResponse;
import com.rentaCar.rentaCarBackend.connection.Response;
import com.rentaCar.rentaCarBackend.dto.CarDTO;
import com.rentaCar.rentaCarBackend.service.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/cars") // IZMENA
public class CarController {

        private final CarService carService; // IZMENA

        public CarController(CarService carService) { // IZMENA
                this.carService = carService;
        }

        @PostMapping("/add")
        public ResponseEntity<Response> addCar(@RequestBody CarDTO car) { // IZMENA
                String result = carService.addCar(car); // IZMENA

                HttpStatus status = result.equals("Successfully created a car!") ? HttpStatus.OK : HttpStatus.BAD_REQUEST; // IZMENA

                return ResponseEntity.status(status)
                        .body(HttpResponse.getResponseWithData(result, Map.of("value", result), status));
        }

        @GetMapping("/all")
        public ResponseEntity<Response> getAll() {
                return ResponseEntity.ok(
                        HttpResponse.getResponseWithData(
                                "Successfully found cars!", // IZMENA
                                Map.of("values", carService.getAll()),
                                HttpStatus.OK));
        }

        @GetMapping("/search/{searchRequest}")
        public ResponseEntity<Response> searchCar(@PathVariable("searchRequest") String searchRequest) { // IZMENA
                return ResponseEntity.ok(
                        HttpResponse.getResponseWithData(
                                "Searched cars:", // IZMENA
                                Map.of("values", carService.searchCars(searchRequest)), // IZMENA
                                HttpStatus.OK));
        }

        @PostMapping("/update")
        public ResponseEntity<Response> updateCar(@RequestBody CarDTO car) { // IZMENA
                String result = carService.updateCar(car); // IZMENA

                HttpStatus status = result.equals("Successfully updated the car!") ? HttpStatus.OK : HttpStatus.BAD_REQUEST; // IZMENA

                return ResponseEntity.status(status)
                        .body(HttpResponse.getResponseWithData(result, Map.of("value", result), status));
        }

        @GetMapping("/getCar/{carId}") // IZMENA
        public ResponseEntity<Response> findCarById(@PathVariable("carId") int carId) { // IZMENA
                return ResponseEntity.ok(
                        HttpResponse.getResponseWithData(
                                "Successfully found the car!", // IZMENA
                                Map.of("value", carService.getCar(carId)), // IZMENA
                                HttpStatus.OK));
        }

        @GetMapping("/withStatus/{status}")
        public ResponseEntity<Response> getWithStatus(@PathVariable("status") int status) {
                return ResponseEntity.ok(
                        HttpResponse.getResponseWithData(
                                "Successfully found the cars!", // IZMENA
                                Map.of("values", carService.getWithStatus(status)),
                                HttpStatus.OK));
        }

        @PostMapping("/delete")
        public ResponseEntity<Response> deleteCar(@RequestBody int carId) { // IZMENA
                String result = carService.deleteCar(carId); // IZMENA

                HttpStatus status = result.equals("Successfully deleted the car!") ? HttpStatus.OK : HttpStatus.BAD_REQUEST; // IZMENA

                return ResponseEntity.status(status)
                        .body(HttpResponse.getResponseWithData(result, Map.of("value", result), status));
        }
}