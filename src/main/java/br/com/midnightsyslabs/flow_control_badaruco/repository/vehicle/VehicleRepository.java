package br.com.midnightsyslabs.flow_control_badaruco.repository.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control_badaruco.domain.entity.vehicle.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String>{
    
}
