/* package br.com.midnightsyslabs.flow_control.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import br.com.midnightsyslabs.flow_control.view.ProductionRawMaterialPurchaseView;
import br.com.midnightsyslabs.flow_control.view.ProductionView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionDTO {
    private Integer id;

    private String productName;

    private String productDescription;

    private BigDecimal productQuantity;

    private String productQuantityMeasurementUnit;

    private BigDecimal productCurrentPrice;

    private BigDecimal quantityProduced;

    private BigDecimal grossQuantityProduced;

    private String grossQuantityProduceddMeasurementUnit;

    private List<ProductionRawMaterialPurchaseView> rawMaterialsPurchaseView;

    private LocalDate date;

    private OffsetDateTime createdAt;

    private OffsetDateTime deletedAt;

    private boolean confirmed;

    public ProductionDTO(ProductionView productionView, List<ProductionRawMaterialPurchaseView> prmpv){
        this.id                         = productionView.getId();
        this.productName                = productionView.getProductName();
        this.productDescription         = productionView.getProductDescription();
        this.quantityProduced           = productionView.getQuantityProduced();
        this.grossQuantityProduced      = productionView.getGrossQuantityProduced();
        this.rawMaterialsPurchaseView   = prmpv;
        this.date                       = productionView.getDate();
        this.createdAt                  = productionView.getCreatedAt();
        this.deletedAt                  = productionView.getDeletedAt();
        this.confirmed                   = productionView.isConfirmed();
    }

    public ProductionDTO(ProductionView productionView){
        this.id                                     = productionView.getId();
        this.productName                            = productionView.getProductName();
        this.productDescription                     = productionView.getProductDescription();
        this.productQuantity                        = productionView.getProductQuantity();
        this.productQuantityMeasurementUnit         = productionView.getProductQuantityMeasurementUnit();
        this.productCurrentPrice                    = productionView.getProductCurrentPrice();
        this.quantityProduced                       = productionView.getQuantityProduced();
        this.grossQuantityProduced                  = productionView.getGrossQuantityProduced();
        this.grossQuantityProduceddMeasurementUnit  = productionView.getGrossQuantityMeasurementUnitSymbol();
        this.date                                   = productionView.getDate();
        this.createdAt                              = productionView.getCreatedAt();
        this.deletedAt                              = productionView.getDeletedAt();
        this.confirmed                               = productionView.isConfirmed();
    }
}
 */