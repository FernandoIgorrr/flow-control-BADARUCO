package br.com.midnightsyslabs.flow_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.midnightsyslabs.flow_control.domain.entity.partner.City;

public interface CityRepository extends JpaRepository<City,Short>{
    
}
