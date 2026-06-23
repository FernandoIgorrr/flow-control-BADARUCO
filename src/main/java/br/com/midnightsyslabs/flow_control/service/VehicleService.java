package br.com.midnightsyslabs.flow_control.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.midnightsyslabs.flow_control.domain.entity.vehicle.Vehicle;
import br.com.midnightsyslabs.flow_control.repository.vehicle.VehicleRepository;
import jakarta.transaction.Transactional;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Transactional
    public void saveVehicle(
            String numberPlate,
            String model) {

        String formattedPlate = numberPlate.trim().toUpperCase();

        // Verifica se já existe um veículo com essa placa
        if (vehicleRepository.existsById(formattedPlate)) {
            throw new RuntimeException("Já existe um veículo cadastrado com a placa " + formattedPlate);
        }

        var vehicle = new Vehicle();
        vehicle.setNumberPlate(numberPlate);
        vehicle.setModel(model);
        vehicle.setCreatedAt(OffsetDateTime.now());

        vehicleRepository.save(vehicle);
    }

    @Transactional
    public void editVehicle(Vehicle vehicle,
            String numberPlate,
            String model) {
    }

    @Transactional
    public void disconnectVehicle(Vehicle vehicle) {
        vehicle.setDeletedAt(OffsetDateTime.now());
        vehicleRepository.save(vehicle);
    }

    @Transactional
    public void connectVehicle(Vehicle vehicle) {
        vehicle.setDeletedAt(null);
        vehicleRepository.save(vehicle);
    }

    /* @Transactional
    public void savePayments(
            Map<Employee, TextField> payments,
            LocalDate paymentDate) {
        for (Map.Entry<Employee, TextField> entry : payments.entrySet()) {

            String value = entry.getValue().getText();

            try {

                if (!(value == null || value.isBlank())) {

                    EmployeePayment employeePayment = new EmployeePayment();
                    employeePayment.setSpentCategory(new SpentCategory((short) 2, ""));
                    employeePayment.setCreatedAt(OffsetDateTime.now());
                    employeePayment.setEmployee(entry.getKey());
                    employeePayment.setPayment(new BigDecimal(UtilsService.solveComma(value)));
                    employeePayment.setPaymentChangeDate(paymentDate);

                    if (entry.getKey().getEmployeePaymentHistory() == null) {
                        entry.getKey().setEmployeePaymentHistory(List.of(employeePayment));
                    } else {
                        entry.getKey().getEmployeePaymentHistory().add(employeePayment);
                    }
                }

            } catch (Exception e) {
                throw e;
            }

            employeeRepository.save(entry.getKey());
        }
    } */

    public List<Vehicle> getVehicles() {
        return vehicleRepository.findAll();
    }
}
