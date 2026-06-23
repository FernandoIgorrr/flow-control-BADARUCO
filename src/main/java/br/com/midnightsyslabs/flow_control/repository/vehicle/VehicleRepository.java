package br.com.midnightsyslabs.flow_control.repository.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control.domain.entity.vehicle.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String>{
    
}
