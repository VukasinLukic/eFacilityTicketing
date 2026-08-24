package com.efacility.ticketing.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO implements DomainDTO {
    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private UserDTO user;
}
