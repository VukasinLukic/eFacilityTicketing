package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.ZgradaDTO;
import com.efacility.ticketing.dto.request.CreateZgradaRequest;
import com.efacility.ticketing.dto.request.UpdateZgradaRequest;

import java.util.List;
import java.util.Map;

public interface ZgradaService {
    List<ZgradaDTO> getAll();

    Map<String, Object> getPaged(int page, int size);

    ZgradaDTO getZgrada(Long id);

    ZgradaDTO addZgrada(CreateZgradaRequest request);

    ZgradaDTO updateZgrada(UpdateZgradaRequest request);

    String deleteZgrada(Long id);
}
