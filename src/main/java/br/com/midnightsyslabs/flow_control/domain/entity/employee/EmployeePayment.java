package br.com.midnightsyslabs.flow_control.domain.entity.employee;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import br.com.midnightsyslabs.flow_control.domain.entity.expense.Expense;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.SpentCategory;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePayment implements Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Employee employee;

    @Positive
    @NotNull
    private BigDecimal payment;

    @NotNull
    @Column(columnDefinition = "date")
    private LocalDate paymentChangeDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SpentCategory spentCategory;

    private OffsetDateTime createdAt;

    private OffsetDateTime deletedAt;

    private boolean confirmed;

    @Override
    public BigDecimal getExpense() {
        return payment;
    }

    @Override
    public LocalDate getDate() {
        return paymentChangeDate;
    }

}
