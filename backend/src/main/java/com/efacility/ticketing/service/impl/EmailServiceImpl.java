package com.efacility.ticketing.service.impl;

import com.efacility.ticketing.model.Stan;
import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.enums.StatusTiketa;
import com.efacility.ticketing.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${application.mail.from:}")
    private String from;

    @Value("${application.mail.enabled:true}")
    private boolean enabled;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async("emailExecutor")
    public void sendStatusChangeEmail(Tiket ticket, StatusTiketa oldStatus, StatusTiketa newStatus) {
        try {
            if (!enabled) {
                log.debug("Slanje e-poste je iskljuceno, preskacem obavestenje za tiket {}", ticket.getId());
                return;
            }
            String posiljalac = from == null ? "" : from.trim();
            if (posiljalac.isBlank()) {
                log.warn("MAIL_USERNAME nije postavljen, obavestenje za tiket {} nije poslato", ticket.getId());
                return;
            }
            if (!jeAdresaIspravna(posiljalac)) {
                log.warn("MAIL_USERNAME nije ispravna e-mail adresa (duzina {}), obavestenje za tiket {} nije poslato."
                        + " Ocekuje se npr. ime.prezime@gmail.com", posiljalac.length(), ticket.getId());
                return;
            }

            Korisnik tenant = ticket.getTenant();
            String primalac = tenant == null || tenant.getEmail() == null ? "" : tenant.getEmail().trim();
            if (primalac.isBlank()) {
                log.warn("Tiket {} nema stanara sa e-mail adresom, obavestenje nije poslato", ticket.getId());
                return;
            }
            if (!jeAdresaIspravna(primalac)) {
                log.warn("Stanar na tiketu {} nema ispravnu e-mail adresu, obavestenje nije poslato", ticket.getId());
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(posiljalac);
            message.setTo(primalac);
            message.setSubject("Promena statusa tiketa: " + ticket.getTitle());
            message.setText(buildBody(ticket, tenant, oldStatus, newStatus));

            mailSender.send(message);
            log.info("Obavestenje o promeni statusa tiketa {} poslato na {}", ticket.getId(), primalac);
        } catch (Exception e) {
            log.error("Slanje obavestenja za tiket {} nije uspelo: {}",
                    ticket != null ? ticket.getId() : null, e.getMessage(), e);
        }
    }

    private String buildBody(Tiket ticket, Korisnik tenant, StatusTiketa oldStatus, StatusTiketa newStatus) {
        StringBuilder sb = new StringBuilder();
        sb.append("Postovani ").append(tenant.getFirstName()).append(" ").append(tenant.getLastName()).append(",\n\n");
        sb.append("status Vaseg prijavljenog kvara je promenjen.\n\n");
        sb.append("Tiket: ").append(ticket.getTitle()).append("\n");
        sb.append("Status: ").append(opis(oldStatus)).append(" -> ").append(opis(newStatus)).append("\n");

        Stan apartment = ticket.getApartment();
        if (apartment != null) {
            String buildingName = apartment.getBuilding() != null ? apartment.getBuilding().getName() : "-";
            sb.append("Zgrada: ").append(buildingName).append("\n");
            sb.append("Stan: ").append(apartment.getNumber()).append("\n");
        }

        sb.append("\nOvo je automatska poruka sistema eFacility Ticketing.");
        return sb.toString();
    }

    private boolean jeAdresaIspravna(String address) {
        return address.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+");
    }

    private String opis(StatusTiketa status) {
        if (status == null) {
            return "-";
        }
        return switch (status) {
            case OPEN -> "Otvoren";
            case ASSIGNED -> "Dodeljen";
            case IN_PROGRESS -> "U toku";
            case COMPLETED -> "Zavrsen";
            case CLOSED -> "Zatvoren";
        };
    }
}
