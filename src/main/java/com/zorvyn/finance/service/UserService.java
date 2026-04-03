package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.request.RegisterRequest;
import com.zorvyn.finance.dto.response.UserResponse;
import com.zorvyn.finance.entity.User;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import com.zorvyn.finance.exception.UserAlreadyExistsException;
import com.zorvyn.finance.repository.UserRepository;
import com.zorvyn.finance.security.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse createUser(RegisterRequest request) {
        log.info("Admin creating new user: {}", request.getUsername());
        
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.VIEWER)
                .active(true)
                .build();

        userRepository.save(user);
        return mapToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return mapToResponse(user);
    }

    public UserResponse updateRole(String id, Role role) {
        log.info("Updating role for user ID: {} to {}", id, role);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setRole(role);
        userRepository.save(user);
        return mapToResponse(user);
    }

    public UserResponse toggleStatus(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setActive(!user.isActive());
        log.info("User {} status toggled to: {}", user.getUsername(), user.isActive());
        userRepository.save(user);
        return mapToResponse(user);
    }

    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        // Logical soft-delete by deactivating and prefixing username
        user.setActive(false);
        user.setUsername("DELETED_" + user.getUsername() + "_" + System.currentTimeMillis());
        log.warn("Soft deleting user: {}", id);
        userRepository.save(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }
}
