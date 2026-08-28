package com.efacility.ticketing.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "E-mail je obavezan")
    @Email(message = "E-mail adresa nije ispravna")
    private String email;

    @NotBlank(message = "Lozinka je obavezna")
    private String password;
}
