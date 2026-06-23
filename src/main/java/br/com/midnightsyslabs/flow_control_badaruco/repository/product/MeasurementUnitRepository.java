package br.com.midnightsyslabs.flow_control_badaruco.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.midnightsyslabs.flow_control_badaruco.domain.entity.product.MeasurementUnit;

public interface MeasurementUnitRepository extends JpaRepository<MeasurementUnit,Short>{
    
}
