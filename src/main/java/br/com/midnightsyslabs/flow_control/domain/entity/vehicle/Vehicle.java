package br.com.midnightsyslabs.flow_control.domain.entity.vehicle;

import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
public class Vehicle {
    @Id
    private String numberPlate;

    @NotNull
    private String model;

    @NotNull
    private OffsetDateTime createdAt;

    private OffsetDateTime deletedAt;

    private boolean confirmed;

}
