package com.rentaCar.rentaCarBackend.controller;

import com.rentaCar.rentaCarBackend.connection.HttpResponse;
import com.rentaCar.rentaCarBackend.connection.Response;
import com.rentaCar.rentaCarBackend.dto.ClientDTO;
import com.rentaCar.rentaCarBackend.dto.RentalDTO;
import com.rentaCar.rentaCarBackend.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/rentals") // IZMENA
public class RentalController {

        private final RentalService rentalService; // IZMENA

        public RentalController(RentalService rentalService) { // IZMENA
                this.rentalService = rentalService;
        }

        @PostMapping("/add")
        public ResponseEntity<Response> addRental(@RequestBody RentalDTO rental) { // IZMENA
                String result = rentalService.addRental(rental); // IZMENA
                HttpStatus status = "Created the rental successfully!".equals(result) ? HttpStatus.OK : HttpStatus.BAD_REQUEST; // IZMENA

                return ResponseEntity.status(status)
                        .body(HttpResponse.getResponseWithData(result, Map.of("value", result), status));
        }

        @GetMapping("/all")
        public ResponseEntity<Response> getAll() {
                return ResponseEntity.ok(
                        HttpResponse.getResponseWithData(
                                "Found rentals!", // IZMENA
                                Map.of("values", rentalService.getAll()),
                                HttpStatus.OK));
        }

        @PostMapping("/update")
        public ResponseEntity<Response> updateRental(@RequestBody RentalDTO rental) { // IZMENA
                return ResponseEntity.ok(
                        HttpResponse.getResponseWithData(
                                "Successfully updated the rental!", // IZMENA
                                Map.of("value", rentalService.updateRental(rental)), // IZMENA
                                HttpStatus.OK));
        }

        @GetMapping("/getRentalStatus/{status}") // IZMENA
        public ResponseEntity<Response> getRentalStatus(@PathVariable("status") int status) { // IZMENA
                return ResponseEntity.ok(
                        HttpResponse.getResponseWithData(
                                "Rentals have been found!", // IZMENA
                                Map.of("values", rentalService.getRentalStatus(status)), // IZMENA
                                HttpStatus.OK));
        }

        @GetMapping("/getRental/{rentalId}") // IZMENA
        public ResponseEntity<Response> findRentalById(@PathVariable("rentalId") int rentalId) { // IZMENA
                return ResponseEntity.ok(
                        HttpResponse.getResponseWithData(
                                "Rental found!", // IZMENA
                                Map.of("value", rentalService.getRental(rentalId)), // IZMENA
                                HttpStatus.OK));
        }

        @PostMapping("/delete")
        public ResponseEntity<Response> deleteRental(@RequestBody int rentalId) { // IZMENA
                String result = rentalService.deleteRental(rentalId); // IZMENA
                HttpStatus status = "Deleted the rental successfully!".equals(result) ? HttpStatus.OK : HttpStatus.BAD_REQUEST; // IZMENA

                return ResponseEntity.status(status)
                        .body(HttpResponse.getResponseWithData(result, Map.of("value", result), status));
        }

        @PostMapping("/getClientsRentals") // IZMENA
        public ResponseEntity<Response> getClientsRentals(@RequestBody ClientDTO client) { // IZMENA
                return ResponseEntity.ok(
                        HttpResponse.getResponseWithData(
                                "Rentals for the client have been found!", // IZMENA
                                Map.of("values", rentalService.getClientsRentals(client)), // IZMENA
                                HttpStatus.OK));
        }
}