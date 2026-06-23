package br.com.midnightsyslabs.flow_control.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.midnightsyslabs.flow_control.view.PurchaseView;

public interface PurchaseViewRepository extends JpaRepository<PurchaseView,Integer>{
    
}
