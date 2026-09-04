package com.efacility.ticketing.service;

import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.enums.StatusTiketa;

public interface EmailService {
    void sendStatusChangeEmail(Tiket ticket, StatusTiketa oldStatus, StatusTiketa newStatus);
}
