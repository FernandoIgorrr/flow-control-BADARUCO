package br.com.midnightsyslabs.flow_control.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import br.com.midnightsyslabs.flow_control.domain.entity.expense.Expense;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.SpentCategory;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.VehicleSpent;
import br.com.midnightsyslabs.flow_control.domain.entity.vehicle.Vehicle;
import br.com.midnightsyslabs.flow_control.dto.SaleDTO;
import br.com.midnightsyslabs.flow_control.repository.vehicle.VehicleRepository;
import br.com.midnightsyslabs.flow_control.repository.vehicle.VehicleSpentRepository;
import br.com.midnightsyslabs.flow_control.repository.view.VehicleSpentViewRepository;
import br.com.midnightsyslabs.flow_control.view.PurchaseView;
import br.com.midnightsyslabs.flow_control.view.VehicleSpentView;
import jakarta.transaction.Transactional;

@Service
public class VehicleSpentService {
    @Autowired
    private VehicleSpentRepository vehicleSpentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleSpentViewRepository vehicleSpentViewRepository;

    @Transactional
    public void saveVehicleSpent(
            String amountPaidStr,
            String quantityStr,
            SpentCategory category,
            Vehicle vehicle,
            String note,
            LocalDate date) {

        // Validações básicas de segurança antes de prosseguir
        if (amountPaidStr == null || amountPaidStr.isBlank()) {
            throw new IllegalArgumentException("O valor pago não pode ser nulo ou vazio.");
        }
        if (quantityStr == null || quantityStr.isBlank()) {
            throw new IllegalArgumentException("A quantidade não pode ser nula ou vazia.");
        }
        if (category == null) {
            throw new IllegalArgumentException("A categoria da despesa deve ser informada.");
        }
        if (vehicle == null) {
            throw new IllegalArgumentException("O veículo deve ser informado.");
        }

        try {
            // Tratando strings numéricas (removendo máscaras ou vírgulas se necessário)
            String cleanAmount = UtilsService.solveComma(amountPaidStr);
            String cleanQuantity = UtilsService.solveComma(quantityStr);

            VehicleSpent vehicleSpent = new VehicleSpent();
            
            // Atribuição dos valores tratados
            vehicleSpent.setAmountPaid(new BigDecimal(cleanAmount));
            vehicleSpent.setQuantity(new BigDecimal(cleanQuantity));
            vehicleSpent.setCategory(category);
            vehicleSpent.setVehicle(vehicle);
            vehicleSpent.setNote(note != null ? note.trim() : null);
            
            // Se a data do DatePicker vier nula por algum motivo, assume o dia atual
            vehicleSpent.setDate(date != null ? date : LocalDate.now());
            
            // Dados de auditoria do sistema
            vehicleSpent.setCreatedAt(OffsetDateTime.now());
            vehicleSpent.setConfirmed(false); // Mantém como falso até aprovação posterior, conforme o seu padrão

            // Salvando no banco de dados
            vehicleSpentRepository.save(vehicleSpent);

        } catch (NumberFormatException e) {
            throw new RuntimeException("Erro ao converter os valores numéricos. Verifique o formato digitado.");
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível salvar o gasto do veículo: " + e.getMessage());
        }

    }

   @Transactional
    public void updateVehicleSpent(
            VehicleSpent vehicleSpent,
            String amountPaidStr,
            String quantityStr,
            SpentCategory category,
            Vehicle vehicle,
            LocalDate date,
            String note) {

        var amountPaid_ = solveComma(amountPaidStr);
        var quantity_ = solveComma(quantityStr);

        vehicleSpent.setCategory(category);
        vehicleSpent.setVehicle(vehicle);

        try {
            vehicleSpent.setAmountPaid(new BigDecimal(amountPaid_));
            vehicleSpent.setQuantity(new BigDecimal(quantity_));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Tem algo errado com o formato do preço ou com a quantidade!");
        }

        vehicleSpent.setDate(date);
        vehicleSpent.setNote(note);

        vehicleSpentRepository.save(vehicleSpent);
    }

    @Transactional
    public void confirmVehicleSpent(VehicleSpent vehicleSpent) {
        vehicleSpent.setConfirmed(true);
        vehicleSpentRepository.save(vehicleSpent);
    }

    @Transactional
    public void deleteVehicleSpent(VehicleSpent vehicleSpent) {
        vehicleSpent.setDeletedAt(OffsetDateTime.now());
        vehicleSpentRepository.save(vehicleSpent);
    }

    // ==========================================
    // MÉTODOS DE BUSCA E LISTAGEM (GETTERS)
    // ==========================================

    public Optional<VehicleSpent> findById(Integer id) {
        return vehicleSpentRepository.findById(id);
    }

    public Optional<VehicleSpent> getById(Integer id) {
        return vehicleSpentRepository.findById(id);
    }

    public List<VehicleSpent> getVehicleSpents() {
        return vehicleSpentRepository.findAll();
    }
    public List<VehicleSpentView> getVehicleSpentView() {
        return vehicleSpentViewRepository.findAll();
    }

    public Page<VehicleSpent> getVehicleSpentsPaged(int page, int size) {
        return vehicleSpentRepository.findAll(PageRequest.of(page, size));
    }

    // Mais antigo primeiro
    public List<VehicleSpent> getVehicleSpentsDateOrdered() {
        return getVehicleSpents()
                .stream()
                .sorted(Comparator.comparing(VehicleSpent::getDate))
                .toList();
    }

    // Mais recente primeiro
    public List<VehicleSpent> getVehicleSpentsDateOrderedReverse() {
        return getVehicleSpents()
                .stream()
                .sorted(
                        Comparator
                                .comparing(VehicleSpent::getDate).reversed()
                                .thenComparing(VehicleSpent::getId, Comparator.reverseOrder())
                )
                .toList();
    }

    public List<VehicleSpent> getVehicleSpentsFromDate(List<VehicleSpent> vehicleSpents, LocalDate startDate, LocalDate endDate) {
        return vehicleSpents.stream()
                .filter(v -> !v.getDate().isBefore(startDate) && !v.getDate().isAfter(endDate))
                .toList();
    }

    // ==========================================
    // CÁLCULOS METRICOS (TOTAIS E LITRAGEM)
    // ==========================================

    public BigDecimal calculateTotalSpent(List<VehicleSpent> vehicleSpents) {
        return vehicleSpents.stream()
                .map(VehicleSpent::getExpense)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalSpentInTime(List<VehicleSpent> vehicleSpents, LocalDate startDate, LocalDate endDate) {
        return getVehicleSpentsFromDate(vehicleSpents, startDate, endDate).stream()
                .map(VehicleSpent::getExpense)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalInLiters(List<VehicleSpent> vehicleSpents) {
        return vehicleSpents.stream()
                .map(VehicleSpent::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Método para tratar a vírgula brasileira para o formato correto do BigDecimal
    private String solveComma(String bigDecimalStr) {
        return bigDecimalStr == null ? "0" : bigDecimalStr.replace(",", ".");
    }

    public List<VehicleSpent> searchBetween(LocalDate start, LocalDate end) {
        return getVehicleSpents().stream().filter(sDTO -> !sDTO.getDate().isBefore(start) && !sDTO.getDate().isAfter(end))
                .toList();
    }

     public List<VehicleSpentView> searchBetweenView(LocalDate start, LocalDate end) {
        return getVehicleSpentView().stream().filter(sDTO -> !sDTO.getDate().isBefore(start) && !sDTO.getDate().isAfter(end))
                .toList();
    }
}
