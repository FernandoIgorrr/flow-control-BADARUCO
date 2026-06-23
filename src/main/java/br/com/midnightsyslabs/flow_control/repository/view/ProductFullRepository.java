package br.com.midnightsyslabs.flow_control.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control.view.ProductView;

@Repository
public interface ProductFullRepository extends JpaRepository<ProductView,Short>{
    
}
