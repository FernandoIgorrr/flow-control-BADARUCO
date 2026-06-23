package br.com.midnightsyslabs.flow_control_badaruco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.midnightsyslabs.flow_control_badaruco.domain.entity.partner.City;

public interface CityRepository extends JpaRepository<City,Short>{
    
}
