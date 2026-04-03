package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.request.RecordRequest;
import com.zorvyn.finance.dto.response.RecordResponse;
import com.zorvyn.finance.entity.FinanceRecord;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import com.zorvyn.finance.repository.FinanceRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecordService {

    @Autowired
    private FinanceRecordRepository recordRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @CacheEvict(value = {"dashboardSummary", "dashboardTrends"}, allEntries = true)
    public RecordResponse createRecord(RecordRequest request) {
        // Extraction of authenticated user directly from SecurityContext for high integrity
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Extracted {} from SecurityContext for new record", username);

        FinanceRecord record = FinanceRecord.builder()
                .amount(request.getAmount())
                .type(request.getType().toUpperCase())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .createdBy(username)
                .build();

        FinanceRecord saved = recordRepository.save(record);
        log.info("Saved new financial record ID: {} by user: {}", saved.getId(), username);
        return mapToResponse(saved);
    }

    public RecordResponse getRecordById(String id) {
        log.debug("Fetching record detail for ID: {}", id);
        FinanceRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with ID: " + id));

        if (record.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Record not found (Soft-Deleted) with ID: " + id);
        }

        return mapToResponse(record);
    }

    public Page<RecordResponse> getAllRecords(String type, String category, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        log.debug("Paginated fetch request for records: type={}, category={}", type, category);
        
        Query query = new Query().with(pageable);
        query.addCriteria(Criteria.where("deletedAt").is(null));

        if (type != null && !type.isEmpty()) {
            query.addCriteria(Criteria.where("type").is(type.toUpperCase()));
        }
        if (category != null && !category.isEmpty()) {
            query.addCriteria(Criteria.where("category").regex(category, "i"));
        }
        if (from != null && to != null) {
            query.addCriteria(Criteria.where("date").gte(from).lte(to));
        }

        List<FinanceRecord> records = mongoTemplate.find(query, FinanceRecord.class);
        
        return PageableExecutionUtils.getPage(
                records.stream().map(this::mapToResponse).collect(Collectors.toList()),
                pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), FinanceRecord.class));
    }

    @CacheEvict(value = {"dashboardSummary", "dashboardTrends"}, allEntries = true)
    public RecordResponse updateRecord(String id, RecordRequest request) {
        log.info("Updating existing record ID: {}", id);
        
        FinanceRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with ID: " + id));

        if (record.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Cannot update soft-deleted record with ID: " + id);
        }

        record.setAmount(request.getAmount());
        record.setType(request.getType().toUpperCase());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNotes(request.getNotes());

        recordRepository.save(record);
        return mapToResponse(record);
    }

    @CacheEvict(value = {"dashboardSummary", "dashboardTrends"}, allEntries = true)
    public void softDeleteRecord(String id) {
        log.warn("Soft deleting record ID: {}", id);
        FinanceRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with ID: " + id));
        record.setDeletedAt(LocalDateTime.now());
        recordRepository.save(record);
    }

    private RecordResponse mapToResponse(FinanceRecord record) {
        return RecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .date(record.getDate())
                .notes(record.getNotes())
                .createdBy(record.getCreatedBy())
                .build();
    }
}
