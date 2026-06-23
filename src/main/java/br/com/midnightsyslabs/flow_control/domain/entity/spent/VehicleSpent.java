package br.com.midnightsyslabs.flow_control.domain.entity.spent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import br.com.midnightsyslabs.flow_control.domain.entity.expense.Expense;
import br.com.midnightsyslabs.flow_control.domain.entity.vehicle.Vehicle;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleSpent implements Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String note;

    @NotNull
    private BigDecimal quantity;

    @NotNull
    private BigDecimal amountPaid;

    @ManyToOne
    @JoinColumn
    private SpentCategory category;

    @ManyToOne
    @JoinColumn
    private Vehicle vehicle;

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
    public SpentCategory getSpentCategory() {
        return category;
    }
}
