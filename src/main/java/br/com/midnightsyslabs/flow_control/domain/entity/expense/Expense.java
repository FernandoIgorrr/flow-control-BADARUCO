package br.com.midnightsyslabs.flow_control.domain.entity.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.midnightsyslabs.flow_control.domain.entity.spent.SpentCategory;

public interface Expense {
    public BigDecimal getExpense();
    public LocalDate getDate();
    public SpentCategory getSpentCategory();
}
