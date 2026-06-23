package br.com.midnightsyslabs.flow_control.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import br.com.midnightsyslabs.flow_control.domain.entity.expense.Expense;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.Spent;
import br.com.midnightsyslabs.flow_control.repository.spent.SpentRepository;
import br.com.midnightsyslabs.flow_control.view.PurchaseView;

@Service
public class ExpenseService {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private VehicleSpentService vehicleSpentService;

    @Autowired
    private SpentRepository spentRepository;

    @Autowired
    private EmployeeService employeeService;

    public List<Expense> getAllExpenses() {

        List<Expense> expenses = new ArrayList<Expense>();

        expenses.addAll(purchaseService.getPurchasesView());
        expenses.addAll(spentRepository.findAll());
        expenses.addAll(employeeService.getEmployeePayments());
        expenses.addAll(vehicleSpentService.getVehicleSpents());

        var result = expenses.stream().sorted(Comparator.comparing(Expense::getDate).reversed())
                .toList();

        return result;
    }

    /*
     * public List<Expense> getAllExpenses(int page, int size) {
     * 
     * List<Expense> result = new ArrayList<Expense>();
     * 
     * result.addAll(purchaseService.getPurchasesViewPaged(page,size).getContent());
     * result.addAll(getSpents(page,size).getContent());
     * result.addAll(employeeService.getEmployeePaymentsPaged(page,size).getContent(
     * ));
     * 
     * return result;
     * }
     */

    private Page<Spent> getSpents(int page, int size) {
        return spentRepository.findAll(PageRequest.of(page, size));
    }

    public List<Expense> searchBetween(LocalDate start, LocalDate end) {
        return getAllExpenses().stream()
                .filter(expense -> !expense.getDate().isBefore(start) && !expense.getDate().isAfter(end)).toList();
    }
}
