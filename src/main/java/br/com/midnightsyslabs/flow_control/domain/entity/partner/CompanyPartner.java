package br.com.midnightsyslabs.flow_control.domain.entity.partner;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CompanyPartner extends Partner {

    @Column(nullable = true)
    private String cnpj;

    @Override
    public String getDocument() {
        return this.cnpj;
    }

    protected CompanyPartner() {
    }

    public CompanyPartner(
            UUID id,
            String name,
            String CNPJ,
            String phone,
            String email,
            City city,
            PartnerRole category,
            OffsetDateTime createdAt,
            OffsetDateTime deletedAt,
            boolean isClosed) {
        super(id, name, email, phone, city, category, createdAt, deletedAt, isClosed);
        this.cnpj = CNPJ;
    }
}
