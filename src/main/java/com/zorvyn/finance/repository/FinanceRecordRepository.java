package com.zorvyn.finance.repository;

import com.zorvyn.finance.entity.FinanceRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface FinanceRecordRepository extends MongoRepository<FinanceRecord, String> {
    
    // Filter out soft-deleted records
    List<FinanceRecord> findAllByDeletedAtIsNull();
    
    List<FinanceRecord> findByTypeAndCategoryAndDateBetweenAndDeletedAtIsNull(
            String type, String category, LocalDateTime start, LocalDateTime end);
    
    List<FinanceRecord> findByDateBetweenAndDeletedAtIsNull(LocalDateTime start, LocalDateTime end);
    
}
