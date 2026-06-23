package br.com.midnightsyslabs.flow_control_badaruco.repository.sale;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control_badaruco.domain.entity.sale.Sale;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Integer> {
}
