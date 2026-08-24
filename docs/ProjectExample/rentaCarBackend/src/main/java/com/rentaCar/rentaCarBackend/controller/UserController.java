package com.rentaCar.rentaCarBackend.controller;

import com.rentaCar.rentaCarBackend.dto.UserDTO;
import com.rentaCar.rentaCarBackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login/{username}/{password}")
    public ResponseEntity<UserDTO> login(@PathVariable String username, @PathVariable String password) {
        try {
            UserDTO user = userService.login(username, password);
            // Ako login uspe, user neće biti null. Ako ne uspe, servis će baciti izuzetak.
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            // Vraća 401 Unauthorized ako podaci nisu ispravni
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}