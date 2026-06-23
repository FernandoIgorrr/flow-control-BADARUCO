package br.com.midnightsyslabs.flow_control.ui.controller.form;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.domain.entity.employee.Employee;
import br.com.midnightsyslabs.flow_control.service.EmployeeService;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@Controller
public class EmployeePaymentsFormController {

    @Autowired
    private EmployeeService employeeService;

    private Runnable onDataChanged;

    @FXML
    private DatePicker datePicker;

    @FXML
    private VBox employeesContainer;

    // Guarda relação funcionário → campo de pagamento
    private final Map<Employee, TextField> paymentsFields = new HashMap<>();

    @FXML
    public void initialize() {
        loadEmployees();
        datePicker.setValue(LocalDate.now());
    }

    private void loadEmployees() {
        List<Employee> employees = employeeService.getEmployees();

        for (Employee employee : employees) {

            TextField nameField = new TextField(employee.getName());
            nameField.setDisable(true);

            TextField wageField = new TextField();
            wageField.setPromptText("Pagamento");
            UiUtils.configurePriceField(wageField);

            HBox row = new HBox(12, nameField, wageField);
            row.setAlignment(Pos.CENTER_LEFT);

            employeesContainer.getChildren().add(row);
            paymentsFields.put(employee, wageField);
        }
    }

    @FXML
    private void onSave() {
        try {
            employeeService.savePayments(paymentsFields, datePicker.getValue());

        } catch (Exception e) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro ao cadastrar pagamentos",
                    "Ocorreu um erro ao tentar cadastrar os pagamentos: " + e.getMessage());
            System.err.println(e.getMessage());
            return;
        }

        close();

        UiUtils.showLabelAlert(Alert.AlertType.INFORMATION, "SUCESSO",
                "Pagamentos registrados com sucesso!");
    }

    @FXML
    private void onCancel() {
        close();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    private void close() {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }
}
