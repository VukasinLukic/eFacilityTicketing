package com.efacility.ticketing.controller;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import com.efacility.ticketing.dto.TiketDTO;
import com.efacility.ticketing.dto.request.AssignTiketRequest;
import com.efacility.ticketing.dto.request.CreateTiketRequest;
import com.efacility.ticketing.dto.request.UpdatePrioritetRequest;
import com.efacility.ticketing.dto.request.UpdateStatusRequest;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.enums.Prioritet;
import com.efacility.ticketing.model.enums.StatusTiketa;
import com.efacility.ticketing.service.TiketExportService;
import com.efacility.ticketing.service.TiketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tickets")
public class TiketController {

    private final TiketService tiketService;
    private final TiketExportService tiketExportService;

    public TiketController(TiketService tiketService, TiketExportService tiketExportService) {
        this.tiketService = tiketService;
        this.tiketExportService = tiketExportService;
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) StatusTiketa status,
            @RequestParam(required = false) Prioritet priority,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal Korisnik currentKorisnik) {
        byte[] body = tiketExportService.exportToExcel(status, priority, buildingId, from, to, currentKorisnik);
        return odgovorSaFajlom(body, imeFajla("tiketi", from, to, "xlsx"),
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) StatusTiketa status,
            @RequestParam(required = false) Prioritet priority,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal Korisnik currentKorisnik) {
        byte[] body = tiketExportService.exportToPdf(status, priority, buildingId, from, to, currentKorisnik);
        return odgovorSaFajlom(body, imeFajla("tiketi", from, to, "pdf"), "application/pdf");
    }

    private ResponseEntity<byte[]> odgovorSaFajlom(byte[] body, String fileName, String contentType) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(body.length)
                .body(body);
    }

    private String imeFajla(String prefix, LocalDate from, LocalDate to, String extension) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder(prefix);
        if (from != null) {
            sb.append("_od_").append(from.format(formatter));
        }
        if (to != null) {
            sb.append("_do_").append(to.format(formatter));
        }
        if (from == null && to == null) {
            sb.append("_").append(LocalDate.now().format(formatter));
        }
        return sb.append(".").append(extension).toString();
    }

    @PostMapping("/create")
    public ResponseEntity<Response> create(@Valid @RequestBody CreateTiketRequest request,
                                           @AuthenticationPrincipal Korisnik currentKorisnik) {
        TiketDTO ticket = tiketService.createTiket(request, currentKorisnik);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Tiket je uspešno kreiran!", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAll(
            @RequestParam(required = false) StatusTiketa status,
            @RequestParam(required = false) Prioritet priority,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<TiketDTO> page = tiketService.getAllTikets(status, priority, buildingId, search, pageable);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Tiketi su učitani.", Map.of(
                        "tickets", page.getContent(),
                        "totalElements", page.getTotalElements(),
                        "totalPages", page.getTotalPages(),
                        "currentPage", page.getNumber(),
                        "pageSize", page.getSize()
                ), HttpStatus.OK)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<Response> getMyTikets(@AuthenticationPrincipal Korisnik currentKorisnik) {
        List<TiketDTO> tickets = tiketService.getMyTikets(currentKorisnik.getId());
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Tiketi su učitani.", Map.of("tickets", tickets), HttpStatus.OK)
        );
    }

    @GetMapping("/assigned")
    public ResponseEntity<Response> getAssigned(@AuthenticationPrincipal Korisnik currentKorisnik) {
        List<TiketDTO> tickets = tiketService.getAssignedTikets(currentKorisnik.getId());
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Tiketi su učitani.", Map.of("tickets", tickets), HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getTiket(@PathVariable Long id,
                                              @AuthenticationPrincipal Korisnik currentKorisnik) {
        TiketDTO ticket = tiketService.getTiket(id, currentKorisnik);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Tiket je učitan.", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }

    @PutMapping("/assign")
    public ResponseEntity<Response> assign(@Valid @RequestBody AssignTiketRequest request,
                                           @AuthenticationPrincipal Korisnik currentKorisnik) {
        TiketDTO ticket = tiketService.assignTechnician(request, currentKorisnik);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Tehničar je uspešno dodeljen!", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<Response> updateStatus(@Valid @RequestBody UpdateStatusRequest request,
                                                 @AuthenticationPrincipal Korisnik currentKorisnik) {
        TiketDTO ticket = tiketService.updateStatus(request, currentKorisnik);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Status je uspešno ažuriran!", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }

    @PutMapping("/updatePriority")
    public ResponseEntity<Response> updatePrioritet(@Valid @RequestBody UpdatePrioritetRequest request,
                                                   @AuthenticationPrincipal Korisnik currentKorisnik) {
        TiketDTO ticket = tiketService.updatePrioritet(request, currentKorisnik);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Prioritet je uspešno ažuriran!", Map.of("ticket", ticket), HttpStatus.OK)
        );
    }
}
