package com.efacility.ticketing.service;

import com.efacility.ticketing.model.Stan;
import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.enums.Prioritet;
import com.efacility.ticketing.model.enums.StatusTiketa;
import com.efacility.ticketing.model.enums.Uloga;
import com.efacility.ticketing.repository.TiketRepository;
import com.efacility.ticketing.repository.spec.TiketSpecifications;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TiketExportService {

    private static final String[] COLUMNS = {
            "Naziv", "Status", "Prioritet", "Zgrada", "Stan",
            "Stanar", "Tehnicar", "Datum kreiranja", "Datum izmene"
    };

    private static final float[] COLUMN_WIDTHS = {18, 9, 9, 13, 6, 14, 14, 10, 10};

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy.");

    private final TiketRepository ticketRepository;

    public TiketExportService(TiketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional(readOnly = true)
    public byte[] exportToExcel(StatusTiketa status, Prioritet priority, Long buildingId,
                                LocalDate from, LocalDate to, Korisnik currentUser) {
        List<Tiket> tickets = ucitajTikete(status, priority, buildingId, from, to, currentUser);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Tiketi");

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < COLUMNS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(COLUMNS[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (Tiket ticket : tickets) {
                Row row = sheet.createRow(rowIndex++);
                String[] values = redZaTiket(ticket);
                for (int i = 0; i < values.length; i++) {
                    row.createCell(i).setCellValue(values[i]);
                }
            }

            for (int i = 0; i < COLUMNS.length; i++) {
                sheet.autoSizeColumn(i);
                int width = sheet.getColumnWidth(i) + 768;
                sheet.setColumnWidth(i, Math.min(width, 15000));
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Generisanje Excel izvestaja nije uspelo.", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] exportToPdf(StatusTiketa status, Prioritet priority, Long buildingId,
                              LocalDate from, LocalDate to, Korisnik currentUser) {
        List<Tiket> tickets = ucitajTikete(status, priority, buildingId, from, to, currentUser);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 24, 24, 36, 24);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

            Paragraph title = new Paragraph("Izvestaj o tiketima", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(6);
            document.add(title);

            Paragraph period = new Paragraph(opisPerioda(from, to), metaFont);
            period.setAlignment(Element.ALIGN_CENTER);
            document.add(period);

            Paragraph generated = new Paragraph(
                    "Generisano: " + LocalDateTime.now().format(DATE_TIME)
                            + "   |   Broj tiketa: " + tickets.size(), metaFont);
            generated.setAlignment(Element.ALIGN_CENTER);
            generated.setSpacingAfter(12);
            document.add(generated);

            PdfPTable table = new PdfPTable(COLUMNS.length);
            table.setWidthPercentage(100);
            table.setWidths(COLUMN_WIDTHS);
            table.setHeaderRows(1);

            for (String column : COLUMNS) {
                PdfPCell cell = new PdfPCell(new Phrase(column, headerFont));
                cell.setBackgroundColor(new Color(225, 229, 235));
                cell.setPadding(4);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            for (Tiket ticket : tickets) {
                for (String value : redZaTiket(ticket)) {
                    PdfPCell cell = new PdfPCell(new Phrase(value, cellFont));
                    cell.setPadding(3);
                    table.addCell(cell);
                }
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Generisanje PDF izvestaja nije uspelo.", e);
        }
    }

    private List<Tiket> ucitajTikete(StatusTiketa status, Prioritet priority, Long buildingId,
                                     LocalDate from, LocalDate to, Korisnik currentUser) {
        Long technicianId = null;
        if (currentUser != null && currentUser.getRole() == Uloga.TECHNICIAN) {
            technicianId = currentUser.getId();
        }

        Specification<Tiket> spec = TiketSpecifications.withFilters(
                status, priority, buildingId, null, from, to, technicianId);

        return ticketRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private String[] redZaTiket(Tiket ticket) {
        Stan apartment = ticket.getApartment();
        String buildingName = apartment != null && apartment.getBuilding() != null
                ? apartment.getBuilding().getName() : "-";
        String apartmentNumber = apartment != null ? apartment.getNumber() : "-";

        return new String[]{
                nullSafe(ticket.getTitle()),
                opisStatusa(ticket.getStatus()),
                opisPrioriteta(ticket.getPriority()),
                buildingName,
                apartmentNumber,
                punoIme(ticket.getTenant()),
                punoIme(ticket.getTechnician()),
                formatiraj(ticket.getCreatedAt()),
                formatiraj(ticket.getUpdatedAt())
        };
    }

    private String opisPerioda(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return "Period: sve vreme";
        }
        String odDela = from != null ? from.format(DATE) : "pocetka";
        String doDela = to != null ? to.format(DATE) : "danas";
        return "Period: " + odDela + " - " + doDela;
    }

    private String punoIme(Korisnik user) {
        if (user == null) {
            return "-";
        }
        return (nullSafe(user.getFirstName()) + " " + nullSafe(user.getLastName())).trim();
    }

    private String formatiraj(LocalDateTime dateTime) {
        return dateTime == null ? "-" : dateTime.format(DATE_TIME);
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private String opisStatusa(StatusTiketa status) {
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

    private String opisPrioriteta(Prioritet priority) {
        if (priority == null) {
            return "-";
        }
        return switch (priority) {
            case LOW -> "Nizak";
            case MEDIUM -> "Srednji";
            case HIGH -> "Visok";
            case URGENT -> "Hitan";
        };
    }
}
