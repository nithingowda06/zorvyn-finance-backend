package com.zorvyn.finance.service;

import com.zorvyn.finance.config.JwtUtil;
import com.zorvyn.finance.dto.request.LoginRequest;
import com.zorvyn.finance.dto.request.RegisterRequest;
import com.zorvyn.finance.dto.response.AuthResponse;
import com.zorvyn.finance.entity.User;
import com.zorvyn.finance.exception.UserAlreadyExistsException;
import com.zorvyn.finance.repository.UserRepository;
import com.zorvyn.finance.security.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getUsername());
        
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Username registration failed: Duplicate user {}", request.getUsername());
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.VIEWER)
                .active(true)
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {} with role {}", user.getUsername(), user.getRole());
        
        return AuthResponse.builder()
                .username(user.getUsername())
                .role(user.getRole().name())
                .token(jwtUtil.generateToken(userDetailsService.loadUserByUsername(user.getUsername())))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("User attempting login: {}", request.getUsername());
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        
        User user = userRepository.findByUsername(request.getUsername()).get();
        log.info("User logged in successfully: {}", request.getUsername());

        return AuthResponse.builder()
                .token(jwt)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}
