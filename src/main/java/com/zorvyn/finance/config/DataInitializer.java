package com.zorvyn.finance.config;

import com.zorvyn.finance.entity.FinanceRecord;
import com.zorvyn.finance.entity.User;
import com.zorvyn.finance.repository.FinanceRecordRepository;
import com.zorvyn.finance.repository.UserRepository;
import com.zorvyn.finance.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinanceRecordRepository recordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            seedUsers();
            seedRecords();
            System.out.println("✅ Initial database seed completed.");
        }
        
        // Ensure 'nithin' user always exists for the user to test
        if (userRepository.findByUsername("nithin").isEmpty()) {
            User nithin = User.builder()
                    .username("nithin")
                    .password(passwordEncoder.encode("nithin"))
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(nithin);
            System.out.println("✅ User 'nithin' created successfully.");
        }
    }

    private void seedUsers() {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .active(true)
                .build();

        User analyst = User.builder()
                .username("analyst")
                .password(passwordEncoder.encode("analyst123"))
                .role(Role.ANALYST)
                .active(true)
                .build();

        User viewer = User.builder()
                .username("viewer")
                .password(passwordEncoder.encode("viewer123"))
                .role(Role.VIEWER)
                .active(true)
                .build();

        userRepository.saveAll(Arrays.asList(admin, analyst, viewer));
    }

    private void seedRecords() {
        LocalDateTime now = LocalDateTime.now();
        
        FinanceRecord r1 = FinanceRecord.builder()
                .amount(new BigDecimal("50000.00"))
                .type("INCOME")
                .category("Salary")
                .date(now.minusMonths(1))
                .notes("March Salary")
                .createdBy("admin")
                .build();

        FinanceRecord r2 = FinanceRecord.builder()
                .amount(new BigDecimal("12000.00"))
                .type("EXPENSE")
                .category("Rent")
                .date(now.minusDays(5))
                .notes("House Rent")
                .createdBy("admin")
                .build();

        FinanceRecord r3 = FinanceRecord.builder()
                .amount(new BigDecimal("2500.00"))
                .type("EXPENSE")
                .category("Food")
                .date(now.minusDays(2))
                .notes("Grocery Shopping")
                .createdBy("admin")
                .build();

        FinanceRecord r4 = FinanceRecord.builder()
                .amount(new BigDecimal("1500.00"))
                .type("EXPENSE")
                .category("Entertainment")
                .date(now.minusDays(1))
                .notes("Netflix Subscription")
                .createdBy("admin")
                .build();

        recordRepository.saveAll(Arrays.asList(r1, r2, r3, r4));
    }
}
