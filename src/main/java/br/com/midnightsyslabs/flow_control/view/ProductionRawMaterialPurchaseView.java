/* package br.com.midnightsyslabs.flow_control.view;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.Immutable;

import br.com.midnightsyslabs.flow_control.domain.entity.expense.Expense;
import br.com.midnightsyslabs.flow_control.domain.entity.production.ProductionRawMaterialsPurchaseId;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.SpentCategory;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Immutable
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="production_raw_material_purchase_full")
public class ProductionRawMaterialPurchaseView implements Expense{

    @EmbeddedId
    private ProductionRawMaterialsPurchaseId id;

    private String rawMaterialName;

    private String rawMaterialDescription;

    private BigDecimal rawMaterialPricePerUnit;

    private BigDecimal rawMaterialQuantity;

    private String measurementSymbol;
   
    private BigDecimal quantityUsed;

    @Override
    public BigDecimal getExpense() {
        return rawMaterialPricePerUnit.multiply(quantityUsed);
    }

    @Override
    public LocalDate getDate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDate' in ProductionRawMaterialPurchaseView");
    }

    @Override
    public SpentCategory getSpentCategory() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSpentCategory' in ProductionRawMaterialPurchaseView");
    }

}
 */