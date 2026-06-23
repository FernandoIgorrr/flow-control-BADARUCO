package br.com.midnightsyslabs.flow_control.domain.entity.spent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import br.com.midnightsyslabs.flow_control.domain.entity.expense.Expense;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Spent implements Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String description;

    private BigDecimal amountPaid;

    @ManyToOne
    @JoinColumn
    private SpentCategory category;

    private LocalDate date;

    private OffsetDateTime createdAt;

    private OffsetDateTime deletedAt;

    private boolean confirmed;

    @Override
    public BigDecimal getExpense() {
        return amountPaid;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public SpentCategory getSpentCategory(){
        return category;
    }

}
