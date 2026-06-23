package br.com.midnightsyslabs.flow_control_badaruco.repository.spent;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.midnightsyslabs.flow_control_badaruco.domain.entity.spent.SpentCategory;

public interface SpentCategoryRepository extends JpaRepository<SpentCategory,Short>{

    
}
