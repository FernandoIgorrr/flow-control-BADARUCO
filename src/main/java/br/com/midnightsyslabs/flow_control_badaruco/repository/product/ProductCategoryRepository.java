package br.com.midnightsyslabs.flow_control_badaruco.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control_badaruco.domain.entity.product.ProductCategory;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Short>{
    
}
