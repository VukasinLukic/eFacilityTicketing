package com.efacility.ticketing.dto;

import com.efacility.ticketing.model.enums.Uloga;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse implements DomainDTO {
    private String token;
    private Long userId;
    private String email;
    private Uloga role;
    private String firstName;
    private String lastName;
}
