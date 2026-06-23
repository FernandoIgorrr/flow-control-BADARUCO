/* package br.com.midnightsyslabs.flow_control.domain.entity.production;

import java.math.BigDecimal;

import br.com.midnightsyslabs.flow_control.domain.entity.purchase.Purchase;

import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.MapsId;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionRawMaterialPurchase {

    @EmbeddedId
    private ProductionRawMaterialsPurchaseId id;

    @ManyToOne
    @MapsId("productionId")
    @JoinColumn(name = "production_id", nullable = false)
    private Production production;

    @ManyToOne
    @MapsId("purchaseId")
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @Positive
    @NotNull
    private BigDecimal quantityUsed;
}

 */