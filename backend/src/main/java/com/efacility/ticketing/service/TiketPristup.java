package com.efacility.ticketing.service;

import com.efacility.ticketing.exception.TiketAccessDeniedException;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.enums.Uloga;
import org.springframework.stereotype.Component;

@Component
public class TiketPristup {

    public void proveriPristup(Tiket ticket, Korisnik korisnik) {
        switch (korisnik.getRole()) {
            case MANAGER -> {
            }
            case TENANT -> {
                if (!ticket.getTenant().getId().equals(korisnik.getId())) {
                    throw new TiketAccessDeniedException("Možete pristupiti samo svojim tiketima!");
                }
            }
            case TECHNICIAN -> {
                if (ticket.getTechnician() == null
                        || !ticket.getTechnician().getId().equals(korisnik.getId())) {
                    throw new TiketAccessDeniedException(
                            "Možete pristupiti samo tiketima koji su vam dodeljeni!");
                }
            }
        }
    }

    public boolean jeMenadzer(Korisnik korisnik) {
        return korisnik.getRole() == Uloga.MANAGER;
    }
}
