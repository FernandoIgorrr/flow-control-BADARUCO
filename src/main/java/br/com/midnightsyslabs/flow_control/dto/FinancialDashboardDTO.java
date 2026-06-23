package br.com.midnightsyslabs.flow_control.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinancialDashboardDTO {
    private BigDecimal totalRevenue;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private Map<String, BigDecimal> expensesByCategory;
    private Map<LocalDate, BigDecimal> revenueTimeline;
    private Map<LocalDate, BigDecimal> expenseTimeline;
    
    // Getters e Setters
}