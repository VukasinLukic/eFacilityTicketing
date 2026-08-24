package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.CommentDTO;
import com.efacility.ticketing.dto.request.AddCommentRequest;
import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.exception.TicketAccessDeniedException;
import com.efacility.ticketing.mapper.CommentMapper;
import com.efacility.ticketing.model.Comment;
import com.efacility.ticketing.model.Ticket;
import com.efacility.ticketing.model.User;
import com.efacility.ticketing.model.enums.Role;
import com.efacility.ticketing.repository.CommentRepository;
import com.efacility.ticketing.repository.TicketRepository;
import com.efacility.ticketing.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository,
                          TicketRepository ticketRepository,
                          UserRepository userRepository,
                          CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByTicket(Long ticketId, User currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        checkTicketAccess(ticket, currentUser);
        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(commentMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    public CommentDTO addComment(AddCommentRequest request, User currentUser) {
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + request.getTicketId()));
        checkTicketAccess(ticket, currentUser);

        User managedUser = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setMessage(request.getMessage());
        comment.setTicket(ticket);
        comment.setUser(managedUser);

        Comment saved = commentRepository.save(comment);
        return commentMapper.toDomainDTO(saved);
    }

    private void checkTicketAccess(Ticket ticket, User currentUser) {
        if (currentUser.getRole() == Role.MANAGER) {
            return;
        }
        if (currentUser.getRole() == Role.TENANT) {
            if (!ticket.getTenant().getId().equals(currentUser.getId())) {
                throw new TicketAccessDeniedException("You can only access your own tickets");
            }
            return;
        }
        if (currentUser.getRole() == Role.TECHNICIAN) {
            if (ticket.getTechnician() == null || !ticket.getTechnician().getId().equals(currentUser.getId())) {
                throw new TicketAccessDeniedException("You can only access tickets assigned to you");
            }
        }
    }
}
