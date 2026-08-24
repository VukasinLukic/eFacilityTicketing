package com.rentaCar.rentaCarBackend.service;

import com.rentaCar.rentaCarBackend.JPARepo.UserRepository;
import com.rentaCar.rentaCarBackend.dto.UserDTO;
import com.rentaCar.rentaCarBackend.mapper.UserMapper;
import com.rentaCar.rentaCarBackend.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Saki
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepo, UserMapper userMapper) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;
    }

    /*
     * public String login(String username, String password) {
     * try {
     * User user = userRepo.findByUsername(username);
     * if (user != null && user.getPassword().equals(password)) {
     * return "Hello, " + user.getName() + " " + user.getSurname() + "!";
     * }
     * } catch (Exception ex) {
     * return "Invalid username or password!";
     * }
     * return "Invalid username or password!";
     * }
     */
    public UserDTO login(String username, String password) {
        User user = userRepo.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setSurname(user.getSurname());
            dto.setUsername(user.getUsername());
            return dto;
        }
        throw new RuntimeException("Invalid username or password");
    }

}
