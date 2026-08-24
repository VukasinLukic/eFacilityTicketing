package com.efacility.ticketing.dto.request;

import com.efacility.ticketing.model.enums.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePriorityRequest {

    @NotNull
    private Long ticketId;

    @NotNull
    private Priority priority;
}
