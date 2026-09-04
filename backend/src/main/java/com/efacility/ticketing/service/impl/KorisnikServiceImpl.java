package com.efacility.ticketing.service.impl;

import com.efacility.ticketing.dto.KorisnikDTO;
import com.efacility.ticketing.mapper.KorisnikMapper;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.enums.Uloga;
import com.efacility.ticketing.repository.KorisnikRepository;
import com.efacility.ticketing.service.KorisnikService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class KorisnikServiceImpl implements KorisnikService {

    private final KorisnikRepository userRepository;
    private final KorisnikMapper userMapper;

    public KorisnikServiceImpl(KorisnikRepository userRepository, KorisnikMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public KorisnikDTO getCurrentKorisnik(Korisnik currentKorisnik) {
        return userMapper.toDomainDTO(currentKorisnik);
    }

    public List<KorisnikDTO> getAllTechnicians() {
        return userRepository.findByRole(Uloga.TECHNICIAN)
                .stream()
                .map(userMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    public List<KorisnikDTO> getAllTenants() {
        return userRepository.findByRole(Uloga.TENANT)
                .stream()
                .map(userMapper::toDomainDTO)
                .collect(Collectors.toList());
    }
}
