package br.com.midnightsyslabs.flow_control.repository.partner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control.domain.entity.partner.CompanyPartner;

@Repository
public interface CompanyPartnerRepository extends JpaRepository<CompanyPartner, UUID> {
    @Override
    List<CompanyPartner> findAll();
    
    @Query("""
    SELECT cp
    FROM CompanyPartner cp
    WHERE cp.role.id = 1
    AND cp.deletedAt = null
""")
    List<CompanyPartner> findAllByRoleClient();
    
    @Query("""
    SELECT cp
    FROM CompanyPartner cp
    WHERE cp.role.id = 2
    AND cp.deletedAt = null
""")
    List<CompanyPartner> findAllByRoleSupplier();
    
    @Override
    Optional<CompanyPartner> findById(UUID id);
}