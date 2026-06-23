package br.com.midnightsyslabs.flow_control.domain.entity.partner;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PartnerRole {
    
    @Id
    private Short id;
    
    private String name;
}
