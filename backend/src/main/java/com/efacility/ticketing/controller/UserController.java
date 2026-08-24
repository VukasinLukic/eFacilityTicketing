package com.efacility.ticketing.controller;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import com.efacility.ticketing.dto.UserDTO;
import com.efacility.ticketing.model.User;
import com.efacility.ticketing.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin("http://localhost:3000")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<Response> getMe(@AuthenticationPrincipal User currentUser) {
        UserDTO user = userService.getCurrentUser(currentUser);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("User fetched", Map.of("user", user), HttpStatus.OK)
        );
    }

    @GetMapping("/technicians")
    public ResponseEntity<Response> getTechnicians() {
        List<UserDTO> technicians = userService.getAllTechnicians();
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Technicians fetched", Map.of("technicians", technicians), HttpStatus.OK)
        );
    }
}
