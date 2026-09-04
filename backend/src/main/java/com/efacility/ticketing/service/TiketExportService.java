package com.efacility.ticketing.service;

import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.enums.Prioritet;
import com.efacility.ticketing.model.enums.StatusTiketa;

import java.time.LocalDate;

public interface TiketExportService {
    byte[] exportToExcel(StatusTiketa status, Prioritet priority, Long buildingId,
                         LocalDate from, LocalDate to, Korisnik currentUser);

    byte[] exportToPdf(StatusTiketa status, Prioritet priority, Long buildingId,
                       LocalDate from, LocalDate to, Korisnik currentUser);
}
