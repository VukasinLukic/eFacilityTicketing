package com.efacility.ticketing.dto;

import com.efacility.ticketing.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse implements DomainDTO {
    private String token;
    private Long userId;
    private String email;
    private Role role;
    private String firstName;
    private String lastName;
}
