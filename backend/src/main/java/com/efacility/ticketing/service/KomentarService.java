package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.KomentarDTO;
import com.efacility.ticketing.dto.request.AddKomentarRequest;
import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.exception.TiketAccessDeniedException;
import com.efacility.ticketing.mapper.KomentarMapper;
import com.efacility.ticketing.model.Komentar;
import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.enums.Uloga;
import com.efacility.ticketing.repository.KomentarRepository;
import com.efacility.ticketing.repository.TiketRepository;
import com.efacility.ticketing.repository.KorisnikRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class KomentarService {

    private final KomentarRepository commentRepository;
    private final TiketRepository ticketRepository;
    private final KorisnikRepository userRepository;
    private final KomentarMapper commentMapper;

    public KomentarService(KomentarRepository commentRepository,
                          TiketRepository ticketRepository,
                          KorisnikRepository userRepository,
                          KomentarMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
    }

    @Transactional(readOnly = true)
    public List<KomentarDTO> getKomentarsByTiket(Long ticketId, Korisnik currentKorisnik) {
        Tiket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        checkTiketAccess(ticket, currentKorisnik);
        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(commentMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    public KomentarDTO addKomentar(AddKomentarRequest request, Korisnik currentKorisnik) {
        Tiket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + request.getTicketId()));
        checkTiketAccess(ticket, currentKorisnik);

        Korisnik managedKorisnik = userRepository.findById(currentKorisnik.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Komentar comment = new Komentar();
        comment.setMessage(request.getMessage());
        comment.setTicket(ticket);
        comment.setUser(managedKorisnik);

        Komentar saved = commentRepository.save(comment);
        return commentMapper.toDomainDTO(saved);
    }

    private void checkTiketAccess(Tiket ticket, Korisnik currentKorisnik) {
        if (currentKorisnik.getRole() == Uloga.MANAGER) {
            return;
        }
        if (currentKorisnik.getRole() == Uloga.TENANT) {
            if (!ticket.getTenant().getId().equals(currentKorisnik.getId())) {
                throw new TiketAccessDeniedException("You can only access your own tickets");
            }
            return;
        }
        if (currentKorisnik.getRole() == Uloga.TECHNICIAN) {
            if (ticket.getTechnician() == null || !ticket.getTechnician().getId().equals(currentKorisnik.getId())) {
                throw new TiketAccessDeniedException("You can only access tickets assigned to you");
            }
        }
    }
}
