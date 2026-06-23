package br.com.midnightsyslabs.flow_control.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control.domain.entity.raw_material.RawMaterial;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial,Short>{
    @Query("""
            SELECT rm FROM RawMaterial rm
    WHERE rm.deletedAt IS NULL
            """)
    List<RawMaterial> findAllNonDeleted();

    Optional<RawMaterial> findByName(String name);
}
