package br.com.midnightsyslabs.flow_control.repository.vehicle;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control.domain.entity.spent.VehicleSpent;

@Repository
public interface VehicleSpentRepository extends JpaRepository<VehicleSpent,Integer>{
       @Override
    @Query("""
            SELECT vs FROM VehicleSpent vs
             WHERE vs.deletedAt IS NULL""")
    public List<VehicleSpent> findAll();
}
