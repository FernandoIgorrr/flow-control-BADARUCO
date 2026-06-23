package br.com.midnightsyslabs.flow_control.view;

import java.math.BigDecimal;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name ="sale_product_full")
public class SaleProductView{

    @Id
    private Integer id;

    private Integer saleId;

    private Short productId;

    private String productName;
    
    private String productDescription;

    private BigDecimal productWeight;

    private String productMeasurementUnitUnit;

    private String productMeasurementUnitName;

    private String productMeasurementUnitPluralName;

    private String productMeasurementUnitSymbol;

    private BigDecimal productPriceOnSaleDate;
    
    private BigDecimal productQuantitySold;

}
