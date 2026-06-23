package br.com.midnightsyslabs.flow_control.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.midnightsyslabs.flow_control.view.SaleProductView;

public interface SaleProductViewRepository extends JpaRepository<SaleProductView,Integer> {
      @Query("""
            SELECT spv
            FROM SaleProductView spv
            WHERE spv.saleId = :id""")
    List<SaleProductView> findAllBySaleId(@Param("id") Integer id);
}
