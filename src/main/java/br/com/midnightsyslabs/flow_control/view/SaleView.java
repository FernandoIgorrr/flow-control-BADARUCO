package br.com.midnightsyslabs.flow_control.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Immutable;

import br.com.midnightsyslabs.flow_control.domain.entity.revenue.Revenue;
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
@Table(name ="sale_full")
public class SaleView{
    
    @Id
    private Integer id;

    private UUID clientId;

    private String clientName;

    private String clientCategory;

    private String clientCpf;

    private String clientCnpj;

    private String clientCity;

    private LocalDate date;

    private OffsetDateTime createdAt;

    private OffsetDateTime deletedAt;

    private boolean confirmed;

}
