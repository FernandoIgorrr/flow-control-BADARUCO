package br.com.midnightsyslabs.flow_control_badaruco.repository.partner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control_badaruco.domain.entity.partner.PartnerRole;

@Repository
public interface PartnerRoleRepository extends JpaRepository<PartnerRole,Short>{
    
}
