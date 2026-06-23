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
public class PersonalPartner extends Partner {

    @Column(nullable = true)
    private String cpf;

    @Override
    public String getDocument() {
        return this.cpf;
    }

    protected PersonalPartner() {
    }

    public PersonalPartner(
            UUID id,
            String name,
            String cpf,
            String phone,
            String email,
            City city,
            PartnerRole role,
            OffsetDateTime createAt,
            OffsetDateTime deletedAt,
            boolean isClosed) {
        super(id, name, email, phone, city, role, createAt, deletedAt, isClosed);
        this.cpf = cpf;
    }
}
