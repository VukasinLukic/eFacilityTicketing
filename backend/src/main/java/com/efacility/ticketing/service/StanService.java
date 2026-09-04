package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.StanDTO;
import com.efacility.ticketing.dto.request.CreateStanRequest;
import com.efacility.ticketing.dto.request.UpdateStanRequest;

import java.util.List;

public interface StanService {
    List<StanDTO> getAll();

    List<StanDTO> getByZgrada(Long buildingId);

    List<StanDTO> getMy(Long tenantId);

    StanDTO getStan(Long id);

    StanDTO addStan(CreateStanRequest request);

    StanDTO updateStan(UpdateStanRequest request);

    StanDTO assignTenant(Long apartmentId, Long tenantId);

    String deleteStan(Long id);
}
