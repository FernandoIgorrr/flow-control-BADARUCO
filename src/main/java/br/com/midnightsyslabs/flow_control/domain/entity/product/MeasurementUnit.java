package br.com.midnightsyslabs.flow_control.domain.entity.product;

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
public class MeasurementUnit {

    @Id
    private Short id;

    @NotNull
    private String name;

    @NotNull
    private String pluralName;

    @NotNull
    private String symbol;

    @NotNull
    private String unit;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;

        MeasurementUnit other = (MeasurementUnit) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
