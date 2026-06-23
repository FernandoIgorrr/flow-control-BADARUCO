package br.com.midnightsyslabs.flow_control.view;

import java.math.BigDecimal;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Immutable
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_full")
public class ProductView {
    @Id
    private Short id;
    private String name;
    private String description;
    private String category;
    private BigDecimal currentPrice;
    private BigDecimal quantity;
    private String measurementUnitUnit;
    private String measurementUnitName;
    private String measurementUnitSymbol;
    private boolean confirmed;

}
