package com.zorvyn.finance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "finance_records")
public class FinanceRecord {

    @Id
    private String id;

    private BigDecimal amount;

    private String type;

    private String category;

    private LocalDateTime date;

    private String notes;

    private String createdBy;

    private LocalDateTime deletedAt;

}
