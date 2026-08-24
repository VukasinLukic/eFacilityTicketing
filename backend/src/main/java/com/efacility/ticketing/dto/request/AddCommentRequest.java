package com.efacility.ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCommentRequest {

    @NotNull
    private Long ticketId;

    @NotBlank
    private String message;
}
