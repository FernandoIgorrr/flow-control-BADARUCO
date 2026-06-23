package br.com.midnightsyslabs.flow_control.repository.view;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control.view.ClientView;

@Repository
public interface ClientRepository extends JpaRepository<ClientView, UUID> {
    @Override
    List<ClientView> findAll();
}
