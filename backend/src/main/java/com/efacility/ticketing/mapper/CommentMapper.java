package com.efacility.ticketing.mapper;

import com.efacility.ticketing.dto.CommentDTO;
import com.efacility.ticketing.model.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    CommentDTO toDomainDTO(Comment entity);
}
