package br.com.midnightsyslabs.flow_control.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import br.com.midnightsyslabs.flow_control.domain.entity.revenue.Revenue;
import br.com.midnightsyslabs.flow_control.view.SaleProductView;
import br.com.midnightsyslabs.flow_control.view.SaleView;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaleDTO implements Revenue {

    private Integer id;

    private UUID clientId;

    private String clientName;

    private String clientCategory;

    private String clientCpf;

    private String clientCnpj;

    private String clientCity;

    private List<SaleProductView> saleProductsView;

    private LocalDate date;

    private OffsetDateTime createdAt;

    private OffsetDateTime deletedAt;

    private boolean confirmed;

    public SaleDTO(SaleView saleView) {
        
        id = saleView.getId();

        clientId = saleView.getClientId();
        
        clientName = saleView.getClientName();

        clientCategory = saleView.getClientCategory();

        clientCpf = saleView.getClientCpf();

        clientCnpj = saleView.getClientCnpj();

        clientCity = saleView.getClientCity();

        date = saleView.getDate();

        createdAt = saleView.getCreatedAt();

        deletedAt = saleView.getDeletedAt();

        confirmed = saleView.isConfirmed();
    }

    @Override
    public BigDecimal getRevenue() {
        return saleProductsView.stream().map(spv -> {
            return spv.getProductPriceOnSaleDate().multiply(spv.getProductQuantitySold());
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
