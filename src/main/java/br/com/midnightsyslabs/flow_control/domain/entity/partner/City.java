package br.com.midnightsyslabs.flow_control.domain.entity.partner;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class City {

    @Id
    private Short id;

    @NotNull
    private String name;

}
