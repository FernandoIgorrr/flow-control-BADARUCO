package br.com.midnightsyslabs.flow_control_badaruco.repository.view;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control_badaruco.view.SupplierView;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierView,UUID>{

    
}
