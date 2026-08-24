package com.efacility.ticketing.mapper;

import com.efacility.ticketing.dto.TicketHistoryDTO;
import com.efacility.ticketing.model.TicketHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TicketHistoryMapper {

    TicketHistoryDTO toDomainDTO(TicketHistory entity);
}
