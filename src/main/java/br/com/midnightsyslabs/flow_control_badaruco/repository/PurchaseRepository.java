package br.com.midnightsyslabs.flow_control_badaruco.repository;

import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control_badaruco.domain.entity.purchase.Purchase;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase,Integer>{
     
}
