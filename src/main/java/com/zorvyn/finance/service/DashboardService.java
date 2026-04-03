package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.response.DashboardResponse;
import com.zorvyn.finance.dto.response.RecordResponse;
import com.zorvyn.finance.dto.response.TrendsResponse;
import com.zorvyn.finance.entity.FinanceRecord;
import com.zorvyn.finance.repository.FinanceRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DashboardService handles complex aggregations and analytical data.
 * Caching is applied here because aggregate calculations are computationally expensive on MongoDB.
 * Caches should typically be cleared (evicted) when new records are created or deleted.
 */
@Service
@Slf4j
public class DashboardService {

    @Autowired
    private FinanceRecordRepository recordRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Cacheable(value = "dashboardSummary")
    public DashboardResponse getSummary() {
        log.info("Calculating new dashboard summary (cache miss)");
        
        // Exclude soft-deleted records from all aggregations
        List<FinanceRecord> records = recordRepository.findAll().stream()
                .filter(r -> r.getDeletedAt() == null)
                .collect(Collectors.toList());

        BigDecimal totalIncome = records.stream()
                .filter(r -> "INCOME".equals(r.getType()))
                .map(FinanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = records.stream()
                .filter(r -> "EXPENSE".equals(r.getType()))
                .map(FinanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, DashboardResponse.CategoryDetail> categoryBreakdown = new HashMap<>();
        records.forEach(r -> {
            DashboardResponse.CategoryDetail detail = categoryBreakdown.computeIfAbsent(
                    r.getCategory(), k -> new DashboardResponse.CategoryDetail(BigDecimal.ZERO, BigDecimal.ZERO));
            
            if ("INCOME".equals(r.getType())) {
                detail.setIncome(detail.getIncome().add(r.getAmount()));
            } else {
                detail.setExpense(detail.getExpense().add(r.getAmount()));
            }
        });

        return DashboardResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(totalIncome.subtract(totalExpenses))
                .categories(categoryBreakdown)
                .build();
    }

    @Cacheable(value = "dashboardTrends", key = "#period")
    public TrendsResponse getTrends(String period) {
        log.info("Calculating {} trends (cache miss)", period);
        
        List<FinanceRecord> records = recordRepository.findAll().stream()
                .filter(r -> r.getDeletedAt() == null)
                .collect(Collectors.toList());

        DateTimeFormatter formatter = "weekly".equalsIgnoreCase(period) 
                ? DateTimeFormatter.ofPattern("yyyy-'W'ww") 
                : DateTimeFormatter.ofPattern("yyyy-MM");

        Map<String, TrendsResponse.MonthlyTrend> trendData = new HashMap<>();

        records.forEach(r -> {
            String key = r.getDate().format(formatter);
            TrendsResponse.MonthlyTrend trend = trendData.computeIfAbsent(key, 
                    k -> new TrendsResponse.MonthlyTrend(k, BigDecimal.ZERO, BigDecimal.ZERO));

            if ("INCOME".equals(r.getType())) {
                trend.setTotalIncome(trend.getTotalIncome().add(r.getAmount()));
            } else {
                trend.setTotalExpenses(trend.getTotalExpenses().add(r.getAmount()));
            }
        });

        return TrendsResponse.builder()
                .trends(trendData.values().stream()
                        .sorted((a, b) -> a.getMonth().compareTo(b.getMonth()))
                        .collect(Collectors.toList()))
                .build();
    }

    public List<RecordResponse> getRecentActivity() {
        log.debug("Fetching recent system activity");
        
        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "date"))
                .limit(10)
                .addCriteria(Criteria.where("deletedAt").is(null));

        return mongoTemplate.find(query, FinanceRecord.class).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
