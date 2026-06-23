package br.com.midnightsyslabs.flow_control.repository.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control.domain.entity.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Short> {

    @Query("""
            SELECT p
            FROM Product p
            LEFT JOIN FETCH p.productPriceHistory
            WHERE p.id = :id""")
    Optional<Product> findByIdWithPriceHistory(@Param("id") Short id);

}