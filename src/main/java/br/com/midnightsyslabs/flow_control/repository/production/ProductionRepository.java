package br.com.midnightsyslabs.flow_control.repository.production;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control.domain.entity.production.Production;

@Repository
public interface ProductionRepository extends JpaRepository<Production,Integer>{
 /*     @Query("""
            SELECT p
            FROM Production p
            LEFT JOIN FETCH p.productionRawMaterialsPurchase
            WHERE p.id = :id""")
    Optional<Production> findByIdWithRawMaterialsPurchase(@Param("id") Integer id); */
}
