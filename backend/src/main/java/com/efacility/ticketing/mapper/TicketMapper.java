package com.efacility.ticketing.mapper;

import com.efacility.ticketing.dto.TicketDTO;
import com.efacility.ticketing.model.Ticket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ApartmentMapper.class})
public interface TicketMapper {

    TicketDTO toDomainDTO(Ticket entity);
}
