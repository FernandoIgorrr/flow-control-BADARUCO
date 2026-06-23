package br.com.midnightsyslabs.flow_control.repository.partner;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control.domain.entity.partner.Partner;

@Repository
public interface PartnerRepository extends JpaRepository<Partner,UUID>{
    
}
