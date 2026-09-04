package com.efacility.ticketing.model;

import com.efacility.ticketing.model.enums.Prioritet;
import com.efacility.ticketing.model.enums.StatusTiketa;
import com.efacility.ticketing.model.enums.Uloga;
import com.efacility.ticketing.repository.IstorijaTiketaRepository;
import com.efacility.ticketing.repository.StanRepository;
import com.efacility.ticketing.repository.TiketRepository;
import com.efacility.ticketing.repository.ZgradaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class KompozicijaTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ZgradaRepository buildingRepository;
    @Autowired
    private StanRepository apartmentRepository;
    @Autowired
    private TiketRepository ticketRepository;
    @Autowired
    private IstorijaTiketaRepository historyRepository;

    private Zgrada building;
    private Stan apartment;
    private Korisnik tenant;

    @BeforeEach
    void setUp() {
        building = new Zgrada();
        building.setName("Sunrise Tower");
        building.setAddress("Bulevar kralja Aleksandra 73");
        em.persist(building);

        apartment = new Stan();
        apartment.setNumber("4B");
        apartment.setFloor(3);
        apartment.setBuilding(building);
        em.persist(apartment);

        tenant = new Korisnik();
        tenant.setFirstName("Petar");
        tenant.setLastName("Petrovic");
        tenant.setEmail("petar@example.com");
        tenant.setPassword("hash");
        tenant.setRole(Uloga.TENANT);
        em.persist(tenant);
    }

    private Tiket noviTiket() {
        Tiket ticket = new Tiket();
        ticket.setTitle("Curi slavina");
        ticket.setDescription("Voda curi ispod sudopere vec dva dana.");
        ticket.setStatus(StatusTiketa.OPEN);
        ticket.setPriority(Prioritet.HIGH);
        ticket.setTenant(tenant);
        ticket.setApartment(apartment);
        em.persist(ticket);
        return ticket;
    }

    @Test
    void brisanjeZgradeSaStanovimaJeOdbijenoJerJeVezaObicnaAsocijacija() {
        Long buildingId = building.getId();
        em.flush();
        em.clear();

        Zgrada loaded = buildingRepository.findById(buildingId).orElseThrow();
        assertThat(loaded.getApartments()).hasSize(1);

        assertThatThrownBy(() -> {
            buildingRepository.delete(loaded);
            em.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void brisanjeTiketaSaKomentaromJeOdbijenoJerJeVezaObicnaAsocijacija() {
        Tiket ticket = noviTiket();

        Komentar comment = new Komentar();
        comment.setMessage("Majstor dolazi u utorak.");
        comment.setTicket(ticket);
        comment.setUser(tenant);
        em.persist(comment);

        IstorijaTiketa history = new IstorijaTiketa();
        history.setOldStatus(null);
        history.setNewStatus(StatusTiketa.OPEN);
        history.setTicket(ticket);
        history.setChangedBy(tenant);
        em.persist(history);

        em.flush();
        em.clear();

        Tiket loaded = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertThatThrownBy(() -> {
            ticketRepository.delete(loaded);
            em.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void brisanjeTiketaBezKomentaraKaskadnoBriseNjegovuIstoriju() {
        Tiket ticket = noviTiket();

        IstorijaTiketa history = new IstorijaTiketa();
        history.setOldStatus(null);
        history.setNewStatus(StatusTiketa.OPEN);
        history.setTicket(ticket);
        history.setChangedBy(tenant);
        em.persist(history);

        Long ticketId = ticket.getId();
        em.flush();
        em.clear();

        Tiket loaded = ticketRepository.findById(ticketId).orElseThrow();
        ticketRepository.delete(loaded);
        em.flush();
        em.clear();

        assertThat(ticketRepository.findById(ticketId)).isEmpty();
        assertThat(historyRepository.findByTicketIdOrderByChangedAtAsc(ticketId)).isEmpty();
    }

    @Test
    void tiketMozePostojatiBezIstorijeJerJeKardinalitetNulaIliVise() {
        Tiket ticket = noviTiket();
        em.flush();
        em.clear();

        assertThat(ticketRepository.findById(ticket.getId())).isPresent();
        assertThat(historyRepository.findByTicketIdOrderByChangedAtAsc(ticket.getId())).isEmpty();
    }

    @Test
    void istiBrojStanaNijeDozvoljenDvaputUIstojZgradi() {
        Stan duplikat = new Stan();
        duplikat.setNumber("4B");
        duplikat.setFloor(9);
        duplikat.setBuilding(building);

        assertThatThrownBy(() -> apartmentRepository.saveAndFlush(duplikat))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void istiBrojStanaJesteDozvoljenURazlicitimZgradama() {
        Zgrada druga = new Zgrada();
        druga.setName("Park View");
        druga.setAddress("Njegoseva 12");
        em.persist(druga);

        Stan istiBroj = new Stan();
        istiBroj.setNumber("4B");
        istiBroj.setFloor(3);
        istiBroj.setBuilding(druga);
        em.persist(istiBroj);

        em.flush();

        assertThat(istiBroj.getId()).isNotNull();
    }

    @Test
    void stanMozePrivremenoBitiBezStanara() {
        em.flush();
        em.clear();

        Stan loaded = apartmentRepository.findById(apartment.getId()).orElseThrow();

        assertThat(loaded.getTenant()).isNull();
    }

    @Test
    void jedanStanarMozeImatiViseStanova() {
        apartment.setTenant(tenant);

        Stan drugiStan = new Stan();
        drugiStan.setNumber("5A");
        drugiStan.setFloor(4);
        drugiStan.setBuilding(building);
        drugiStan.setTenant(tenant);
        em.persist(drugiStan);
        em.flush();
        em.clear();

        assertThat(apartmentRepository.findByTenantId(tenant.getId()))
                .extracting(Stan::getNumber)
                .containsExactlyInAnyOrder("4B", "5A");
    }
}
