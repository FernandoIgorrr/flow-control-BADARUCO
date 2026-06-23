package br.com.midnightsyslabs.flow_control.repository.sale;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control.domain.entity.sale.SaleProduct;

@Repository
public interface SaleProductRepository extends JpaRepository<SaleProduct,Integer>{}
