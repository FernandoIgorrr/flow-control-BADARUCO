package br.com.midnightsyslabs.flow_control.repository.partner;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.midnightsyslabs.flow_control.domain.entity.partner.PersonalPartner;

@Repository
public interface PersonalPartnerRepository extends JpaRepository<PersonalPartner, UUID> {
    @Override
    List<PersonalPartner> findAll();

    @Query("""
    SELECT pp
    FROM PersonalPartner pp
    WHERE pp.role.id = 1
""")
    List<PersonalPartner> findAllByRoleClient();

     @Query("""
    SELECT pp
    FROM PersonalPartner pp
    WHERE pp.role.id = 2
""")
    List<PersonalPartner> findAllByRoleSupplier();
}
