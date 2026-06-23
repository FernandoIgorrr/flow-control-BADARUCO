package br.com.midnightsyslabs.flow_control.view;

import java.util.UUID;

import org.hibernate.annotations.Immutable;

import br.com.midnightsyslabs.flow_control.converter.PartnerCategoryConverter;
import jakarta.persistence.Convert;
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
@Table(name = "client_full")
public class ClientView {
    @Id
    private UUID id;
    private String name;
    private String cpf;
    private String cnpj;
    private String phone;
    private String email;
    private String city;
    private boolean confirmed;

    @Convert(converter = PartnerCategoryConverter.class)
    private PartnerCategory category;

    public String getDocument() {
        if (cpf != null && !cpf.isBlank()) {
            return cpf;
        }
        if (cnpj != null && !cnpj.isBlank()) {
            return cnpj;
        }
        return "";
    }
}
