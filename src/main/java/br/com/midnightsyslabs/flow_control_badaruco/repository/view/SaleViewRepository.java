package br.com.midnightsyslabs.flow_control_badaruco.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control_badaruco.view.SaleView;

@Repository
public interface SaleViewRepository extends JpaRepository<SaleView,Integer>{
    
}
