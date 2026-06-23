package br.com.midnightsyslabs.flow_control_badaruco.repository.partner;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control_badaruco.domain.entity.partner.PersonalPartner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
