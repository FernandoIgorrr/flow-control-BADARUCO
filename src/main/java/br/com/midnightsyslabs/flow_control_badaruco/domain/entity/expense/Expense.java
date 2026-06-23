package br.com.midnightsyslabs.flow_control_badaruco.domain.entity.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.midnightsyslabs.flow_control_badaruco.domain.entity.spent.SpentCategory;

public interface Expense {
    public BigDecimal getExpense();
    public LocalDate getDate();
    public SpentCategory getSpentCategory();
}
