package br.com.midnightsyslabs.flow_control.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.midnightsyslabs.flow_control.domain.entity.purchase.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase,Integer>{
     
}
