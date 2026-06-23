/* package br.com.midnightsyslabs.flow_control.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.midnightsyslabs.flow_control.domain.entity.production.ProductionRawMaterialsPurchaseId;
import br.com.midnightsyslabs.flow_control.view.ProductionRawMaterialPurchaseView;

public interface ProductionRawMaterialPurchaseViewRepository
        extends JpaRepository<ProductionRawMaterialPurchaseView, ProductionRawMaterialsPurchaseId> {
    @Query("""
            SELECT p
            FROM ProductionRawMaterialPurchaseView p
            WHERE p.id.productionId = :id""")
    List<ProductionRawMaterialPurchaseView> findAllByProductionId(@Param("id") Integer id);
}
 */