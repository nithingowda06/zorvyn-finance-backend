package com.zorvyn.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordResponse {
    private String id;
    private BigDecimal amount;
    private String type;
    private String category;
    private LocalDateTime date;
    private String notes;
    private String createdBy;
}
