package br.com.midnightsyslabs.flow_control_badaruco.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control_badaruco.view.VehicleSpentView;

@Repository
public interface VehicleSpentViewRepository extends JpaRepository<VehicleSpentView,Integer> {
    
}
