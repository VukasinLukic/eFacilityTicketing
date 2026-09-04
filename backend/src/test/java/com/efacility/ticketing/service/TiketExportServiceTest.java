package com.efacility.ticketing.service;

import com.efacility.ticketing.model.Stan;
import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.Zgrada;
import com.efacility.ticketing.model.enums.Prioritet;
import com.efacility.ticketing.model.enums.StatusTiketa;
import com.efacility.ticketing.model.enums.Uloga;
import com.efacility.ticketing.repository.TiketRepository;
import com.efacility.ticketing.service.impl.TiketExportServiceImpl;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TiketExportServiceTest {

    @Mock
    private TiketRepository ticketRepository;

    private TiketExportService exportService;

    private Korisnik manager;
    private Korisnik technician;
    private List<Tiket> tickets;

    @BeforeEach
    void setUp() {
        exportService = new TiketExportServiceImpl(ticketRepository);

        manager = korisnik(1L, "Marko", "Markovic", Uloga.MANAGER);
        technician = korisnik(2L, "Petar", "Petrovic", Uloga.TECHNICIAN);
        Korisnik tenant = korisnik(3L, "Ana", "Anic", Uloga.TENANT);

        Zgrada building = new Zgrada();
        building.setId(10L);
        building.setName("Zgrada A");
        building.setAddress("Knez Mihailova 1");

        Stan apartment = new Stan();
        apartment.setId(20L);
        apartment.setNumber("4B");
        apartment.setFloor(4);
        apartment.setBuilding(building);

        Tiket prvi = new Tiket();
        prvi.setId(100L);
        prvi.setTitle("Curi slavina");
        prvi.setDescription("Kupatilo");
        prvi.setStatus(StatusTiketa.OPEN);
        prvi.setPriority(Prioritet.HIGH);
        prvi.setTenant(tenant);
        prvi.setTechnician(technician);
        prvi.setApartment(apartment);
        prvi.setCreatedAt(LocalDateTime.of(2026, 3, 1, 10, 0));
        prvi.setUpdatedAt(LocalDateTime.of(2026, 3, 2, 12, 30));

        Tiket drugi = new Tiket();
        drugi.setId(101L);
        drugi.setTitle("Ne radi lift");
        drugi.setDescription("Ulaz 2");
        drugi.setStatus(StatusTiketa.IN_PROGRESS);
        drugi.setPriority(Prioritet.URGENT);
        drugi.setTenant(tenant);
        drugi.setApartment(apartment);
        drugi.setCreatedAt(LocalDateTime.of(2026, 3, 5, 9, 15));
        drugi.setUpdatedAt(LocalDateTime.of(2026, 3, 5, 9, 15));

        tickets = List.of(prvi, drugi);
        when(ticketRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(tickets);
    }

    @Test
    void excelSadrziZaglavljeIRedZaSvakiTiket() throws Exception {
        byte[] bytes = exportService.exportToExcel(null, null, null, null, null, manager);

        assertThat(bytes).isNotEmpty();

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Sheet sheet = workbook.getSheetAt(0);

            assertThat(sheet.getSheetName()).isEqualTo("Tiketi");
            assertThat(sheet.getLastRowNum()).isEqualTo(2);

            Row header = sheet.getRow(0);
            assertThat(header.getCell(0).getStringCellValue()).isEqualTo("Naziv");
            assertThat(header.getCell(6).getStringCellValue()).isEqualTo("Tehnicar");
            assertThat(header.getCell(8).getStringCellValue()).isEqualTo("Datum izmene");

            Row prvi = sheet.getRow(1);
            assertThat(prvi.getCell(0).getStringCellValue()).isEqualTo("Curi slavina");
            assertThat(prvi.getCell(1).getStringCellValue()).isEqualTo("Otvoren");
            assertThat(prvi.getCell(2).getStringCellValue()).isEqualTo("Visok");
            assertThat(prvi.getCell(3).getStringCellValue()).isEqualTo("Zgrada A");
            assertThat(prvi.getCell(4).getStringCellValue()).isEqualTo("4B");
            assertThat(prvi.getCell(5).getStringCellValue()).isEqualTo("Ana Anic");
            assertThat(prvi.getCell(6).getStringCellValue()).isEqualTo("Petar Petrovic");

            Row drugi = sheet.getRow(2);
            assertThat(drugi.getCell(0).getStringCellValue()).isEqualTo("Ne radi lift");
            assertThat(drugi.getCell(6).getStringCellValue()).isEqualTo("-");
        }
    }

    @Test
    void pdfPocinjeZaglavljemPdfDokumenta() {
        byte[] bytes = exportService.exportToPdf(null, null, null,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31), manager);

        assertThat(bytes).isNotEmpty();
        assertThat(new String(bytes, 0, 4, StandardCharsets.ISO_8859_1)).isEqualTo("%PDF");
    }

    @Test
    void tehnicarDobijaFilterSamoNaSvojeTikete() {
        exportService.exportToExcel(null, null, null, null, null, technician);

        Predicate predicate = primeniSpecifikaciju();

        assertThat(predicate).isNotNull();
    }

    @Test
    void menadzerNemaFilterPoTehnicaru() {
        exportService.exportToExcel(null, null, null, null, null, manager);

        ArgumentCaptor<Specification> captor = ArgumentCaptor.forClass(Specification.class);
        verify(ticketRepository).findAll(captor.capture(), any(Sort.class));

        Root<Tiket> root = mockRoot();
        CriteriaQuery<?> query = org.mockito.Mockito.mock(CriteriaQuery.class);
        CriteriaBuilder cb = org.mockito.Mockito.mock(CriteriaBuilder.class);

        captor.getValue().toPredicate(root, query, cb);

        verify(root, never()).get("technician");
        verify(cb, never()).equal(any(Path.class), eq(technician.getId()));
    }

    @SuppressWarnings("unchecked")
    private Predicate primeniSpecifikaciju() {
        ArgumentCaptor<Specification> captor = ArgumentCaptor.forClass(Specification.class);
        verify(ticketRepository).findAll(captor.capture(), any(Sort.class));

        Root<Tiket> root = mockRoot();
        CriteriaQuery<?> query = org.mockito.Mockito.mock(CriteriaQuery.class);
        CriteriaBuilder cb = org.mockito.Mockito.mock(CriteriaBuilder.class);

        Path<Object> technicianPath = org.mockito.Mockito.mock(Path.class);
        Path<Object> idPath = org.mockito.Mockito.mock(Path.class);
        Predicate predicate = org.mockito.Mockito.mock(Predicate.class);

        when(root.get("technician")).thenReturn(technicianPath);
        when(technicianPath.get("id")).thenReturn(idPath);
        when(cb.equal(idPath, technician.getId())).thenReturn(predicate);

        Predicate result = captor.getValue().toPredicate(root, query, cb);

        verify(cb).equal(idPath, technician.getId());
        return result;
    }

    @SuppressWarnings("unchecked")
    private Root<Tiket> mockRoot() {
        return org.mockito.Mockito.mock(Root.class);
    }

    private Korisnik korisnik(Long id, String ime, String prezime, Uloga uloga) {
        Korisnik korisnik = new Korisnik();
        korisnik.setId(id);
        korisnik.setFirstName(ime);
        korisnik.setLastName(prezime);
        korisnik.setEmail(ime.toLowerCase() + "@primer.com");
        korisnik.setRole(uloga);
        return korisnik;
    }
}
