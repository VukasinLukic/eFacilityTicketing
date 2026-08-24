package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.UserDTO;
import com.efacility.ticketing.mapper.UserMapper;
import com.efacility.ticketing.model.User;
import com.efacility.ticketing.model.enums.Role;
import com.efacility.ticketing.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO getCurrentUser(User currentUser) {
        return userMapper.toDomainDTO(currentUser);
    }

    public List<UserDTO> getAllTechnicians() {
        return userRepository.findByRole(Role.TECHNICIAN)
                .stream()
                .map(userMapper::toDomainDTO)
                .collect(Collectors.toList());
    }
}
