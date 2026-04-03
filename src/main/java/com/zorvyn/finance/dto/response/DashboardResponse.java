package com.zorvyn.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private Map<String, CategoryDetail> categories;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryDetail {
        private BigDecimal income;
        private BigDecimal expense;
    }
}
