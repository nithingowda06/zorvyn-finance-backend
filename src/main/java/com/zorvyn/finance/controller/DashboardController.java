package com.zorvyn.finance.controller;

import com.zorvyn.finance.dto.response.DashboardResponse;
import com.zorvyn.finance.dto.response.RecordResponse;
import com.zorvyn.finance.dto.response.TrendsResponse;
import com.zorvyn.finance.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<DashboardResponse> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<TrendsResponse> getTrends(
            @RequestParam(defaultValue = "monthly") String period) {
        return ResponseEntity.ok(dashboardService.getTrends(period));
    }

    @GetMapping("/category-breakdown")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<Map<String, DashboardResponse.CategoryDetail>> getCategoryBreakdown() {
        return ResponseEntity.ok(dashboardService.getSummary().getCategories());
    }

    @GetMapping("/recent-activity")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<List<RecordResponse>> getRecentActivity() {
        return ResponseEntity.ok(dashboardService.getRecentActivity());
    }
}
