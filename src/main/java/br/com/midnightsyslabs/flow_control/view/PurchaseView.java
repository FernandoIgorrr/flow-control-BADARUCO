package br.com.midnightsyslabs.flow_control.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.View;

import br.com.midnightsyslabs.flow_control.converter.PartnerCategoryConverter;
import br.com.midnightsyslabs.flow_control.domain.entity.expense.Expense;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.SpentCategory;
import jakarta.persistence.Convert;
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
@Table(name = "purchase_full")
public class PurchaseView implements Expense{
    @Id
    private Integer id;

    private String partnerName;

    @Convert(converter = PartnerCategoryConverter.class)
    private PartnerCategory partnerCategory;

    private String rawMaterialName;

    private String rawMaterialDescription;

    private BigDecimal quantity;

    private String measurementUnitUnit;

    private String measurementUnitName;

    private String measurementUnitPluralName;

    private String measurementUnitSymbol;

    private BigDecimal pricePerUnit;

    @ManyToOne
    @JoinColumn
    private SpentCategory spentCategory;

    private LocalDate date;

    private String note;

    private OffsetDateTime createdAt;

    private OffsetDateTime deletedAt;

    private boolean confirmed;

    @Override
    public BigDecimal getExpense(){
        return pricePerUnit.multiply(quantity);
    }

    @Override
    public LocalDate getDate(){
        return date;
    }

    @Override
    public SpentCategory getSpentCategory(){
        return spentCategory;
    }
}
