package br.com.midnightsyslabs.flow_control.domain.entity.purchase;

import br.com.midnightsyslabs.flow_control.domain.entity.expense.Expense;
import br.com.midnightsyslabs.flow_control.domain.entity.partner.Partner;
import br.com.midnightsyslabs.flow_control.domain.entity.product.MeasurementUnit;
import br.com.midnightsyslabs.flow_control.domain.entity.raw_material.RawMaterial;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.SpentCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Purchase implements Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    private Partner partner;

    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    private RawMaterial rawMaterial;

    @NotNull
    private BigDecimal quantity;

    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    private MeasurementUnit measurementUnit;

    @NotNull
    private BigDecimal pricePerUnit;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SpentCategory spentCategory;

    @NotNull
    @Column(columnDefinition = "date")
    private LocalDate date;

    private String note;

    private OffsetDateTime createdAt;

    private OffsetDateTime deletedAt;

    private boolean confirmed;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    @Override
    public BigDecimal getExpense() {
        return pricePerUnit.multiply(quantity);
    }

    @Override
    public LocalDate getDate() {
        return date;
    }
}
