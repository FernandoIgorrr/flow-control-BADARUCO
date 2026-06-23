package br.com.midnightsyslabs.flow_control.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.hibernate.annotations.Immutable;

import br.com.midnightsyslabs.flow_control.domain.entity.expense.Expense;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.SpentCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Immutable
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vehicle_spent_full")
public class VehicleSpentView implements Expense{

    @Id
    private Integer id;

    private LocalDate date;

    private String note;

     @ManyToOne
    @JoinColumn
    private SpentCategory spentCategory;

    private OffsetDateTime createdAt;

    private OffsetDateTime deletedAt;

    private BigDecimal quantity;

    private BigDecimal amountPaid;

    private String vehicleNumberPlate;

    private String model;

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
        return spentCategory;
    }
}