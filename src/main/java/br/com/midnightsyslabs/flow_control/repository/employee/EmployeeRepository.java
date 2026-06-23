package br.com.midnightsyslabs.flow_control.repository.employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.midnightsyslabs.flow_control.domain.entity.employee.Employee;
import br.com.midnightsyslabs.flow_control.domain.entity.product.Product;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
        @Query("""
                        SELECT e
                        FROM Employee e
                        LEFT JOIN FETCH e.employeePaymentHistory
                        WHERE e.id = :id AND e.deletedAt IS NULL""")
        Optional<Product> findByIdWithPaymentHistory(@Param("id") UUID id);

        @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.employeePaymentHistory WHERE e.deletedAt IS NULL")
        List<Employee> findAllConnectedWithHistory();

        @Query("""
                SELECT e 
                FROM Employee e 
                LEFT JOIN FETCH e.employeePaymentHistory 
                ORDER BY
                        CASE WHEN e.deletedAt IS NULL THEN 0 ELSE 1 END,
                e.name
        """)
        List<Employee> findAllWithHistory();

        @Override
        @Query("""
                        SELECT e
                        FROM Employee e
                        WHERE e.deletedAt IS NULL
                        """)
        List<Employee> findAll();
}
