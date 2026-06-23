package br.com.midnightsyslabs.flow_control.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control.view.ProductionView;

@Repository
public interface ProductionViewRepository extends JpaRepository<ProductionView,Integer>{
    @Override
    @Query("""
            SELECT pv FROM ProductionView pv WHERE pv.deletedAt IS NULL
            """)
    List<ProductionView> findAll();
    
}
