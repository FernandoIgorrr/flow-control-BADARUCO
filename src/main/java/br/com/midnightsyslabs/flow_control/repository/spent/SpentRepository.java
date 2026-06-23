package br.com.midnightsyslabs.flow_control.repository.spent;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.midnightsyslabs.flow_control.domain.entity.spent.Spent;

public interface SpentRepository extends JpaRepository<Spent, Integer> {
    @Override
    @Query("""
            SELECT s FROM Spent s
             WHERE s.deletedAt IS NULL""")
    public List<Spent> findAll();
}
