package com.zorvyn.finance.controller;

import com.zorvyn.finance.dto.request.RegisterRequest;
import com.zorvyn.finance.dto.response.UserResponse;
import com.zorvyn.finance.security.Role;
import com.zorvyn.finance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "User Management", description = "Admin-only endpoints for managing users and roles")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "Explicitly create a new user (Admin only)")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all users in the system")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user details by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Update a user's role (ANALYST, VIEWER, ADMIN)")
    public ResponseEntity<UserResponse> updateRole(@PathVariable String id, @RequestParam Role role) {
        return ResponseEntity.ok(userService.updateRole(id, role));
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Activate/Deactivate a user account")
    public ResponseEntity<UserResponse> toggleStatus(@PathVariable String id) {
        return ResponseEntity.ok(userService.toggleStatus(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a user account")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
