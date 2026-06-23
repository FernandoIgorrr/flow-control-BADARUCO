package br.com.midnightsyslabs.flow_control.ui.controller.form;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.service.EmployeeService;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@Controller
public class EmployeeFormController {
    @Autowired
    private EmployeeService employeeService;

    private Runnable onDataChanged;

    @FXML
    private TextField nameField;

    @FXML
    private void onSave() {
        if (nameField.getText().isBlank()) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                    "Por favor preencha o nome!");
            return;
        }
        try {
            employeeService.saveEmployee(nameField.getText());

            if (onDataChanged != null) {
                onDataChanged.run();
            }
        } catch (Exception e) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Algo deu errado",
                    e.getMessage());
            return;
        }

        close();

        UiUtils.showLabelAlert(Alert.AlertType.INFORMATION, "SUCESSO",
                "Funcionário cadastrado com sucesso!");
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    @FXML
    private void onCancel() {
        close();
    }

    @FXML
    private void close() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
