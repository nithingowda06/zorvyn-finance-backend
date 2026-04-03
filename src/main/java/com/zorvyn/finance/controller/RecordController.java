package com.zorvyn.finance.controller;

import com.zorvyn.finance.dto.request.RecordRequest;
import com.zorvyn.finance.dto.response.RecordResponse;
import com.zorvyn.finance.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/records")
@Slf4j
@Tag(name = "Financial Records", description = "Endpoints for managing income and expense entries")
@SecurityRequirement(name = "bearerAuth")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(summary = "Create a new financial record", description = "Analyst and Admin roles can create records. Audit user is automatically set from JWT.")
    @ApiResponse(responseCode = "201", description = "Record created successfully")
    public ResponseEntity<RecordResponse> createRecord(@Valid @RequestBody RecordRequest request) {
        log.info("Creating {} record for category: {}", request.getType(), request.getCategory());
        RecordResponse response = recordService.createRecord(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get filtered and paginated records", description = "All roles can view records. Supports filtering by type, category, and date range.")
    public ResponseEntity<Page<RecordResponse>> getAllRecords(
            @Parameter(description = "Filter by type (INCOME/EXPENSE)") @RequestParam(required = false) String type,
            @Parameter(description = "Filter by category name") @RequestParam(required = false) String category,
            @Parameter(description = "Start date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "End date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @Parameter(description = "Zero-indexed page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Records per page") @RequestParam(defaultValue = "10") int size) {
        
        return ResponseEntity.ok(recordService.getAllRecords(type, category, from, to, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single record by ID")
    public ResponseEntity<RecordResponse> getRecordById(@PathVariable String id) {
        return ResponseEntity.ok(recordService.getRecordById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(summary = "Update an existing record")
    public ResponseEntity<RecordResponse> updateRecord(
            @PathVariable String id, 
            @Valid @RequestBody RecordRequest request) {
        log.info("Updating record ID: {}", id);
        return ResponseEntity.ok(recordService.updateRecord(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a record", description = "Only Admins can delete records. This is a soft-delete (deletedAt is set).")
    @ApiResponse(responseCode = "204", description = "Record soft-deleted successfully")
    public ResponseEntity<Void> deleteRecord(@PathVariable String id) {
        log.warn("Deleting record ID: {}", id);
        recordService.softDeleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
