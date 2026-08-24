package com.efacility.ticketing.dto;

import com.efacility.ticketing.model.enums.Role;
import lombok.Data;

@Data
public class UserDTO implements DomainDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
