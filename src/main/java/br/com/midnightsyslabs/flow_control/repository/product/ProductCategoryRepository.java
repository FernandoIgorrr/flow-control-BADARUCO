package br.com.midnightsyslabs.flow_control.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control.domain.entity.product.ProductCategory;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Short>{
    
}
