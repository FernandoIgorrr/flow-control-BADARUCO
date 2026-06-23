package br.com.midnightsyslabs.flow_control.domain.entity.sale;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import br.com.midnightsyslabs.flow_control.domain.entity.partner.Partner;
import br.com.midnightsyslabs.flow_control.domain.entity.revenue.Revenue;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class Sale implements Revenue {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Integer id;

   @NotNull
   @ManyToOne
   @JoinColumn(nullable = false)
   private Partner client;

   @NotNull
   @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<SaleProduct> saleProducts;

   @NotNull
   @Column
   private LocalDate date;

   @NotNull
   @Column
   private OffsetDateTime createdAt;

   @Column
   private OffsetDateTime deletedAt;

   private boolean confirmed;

   @Override
   public BigDecimal getRevenue() {
      return saleProducts.stream()
            .map(saleProduct -> saleProduct.getProductPriceOnSaleDate().multiply(saleProduct.getQuantity()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
   }

   @Override
   public LocalDate getDate() {
      return date;
   }
}
