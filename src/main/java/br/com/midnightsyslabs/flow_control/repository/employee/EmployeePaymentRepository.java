package br.com.midnightsyslabs.flow_control.repository.employee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.midnightsyslabs.flow_control.domain.entity.employee.EmployeePayment;

public interface EmployeePaymentRepository extends JpaRepository<EmployeePayment,Integer>{
    @Override
    @Query("""
            SELECT ep FROM EmployeePayment ep
             WHERE ep.deletedAt IS NULL""")
    public List<EmployeePayment> findAll();
}
