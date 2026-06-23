package br.com.midnightsyslabs.flow_control.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.midnightsyslabs.flow_control.domain.entity.product.MeasurementUnit;

public interface MeasurementUnitRepository extends JpaRepository<MeasurementUnit,Short>{
    
}
