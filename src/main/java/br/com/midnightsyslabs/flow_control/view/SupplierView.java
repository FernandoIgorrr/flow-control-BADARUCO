package br.com.midnightsyslabs.flow_control.view;

import java.util.UUID;

import org.hibernate.annotations.Immutable;

import br.com.midnightsyslabs.flow_control.converter.PartnerCategoryConverter;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Convert;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@Immutable
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "supplier_full")
public class SupplierView {
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
