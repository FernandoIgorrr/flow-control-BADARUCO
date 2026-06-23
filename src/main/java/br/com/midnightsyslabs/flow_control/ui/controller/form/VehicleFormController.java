package br.com.midnightsyslabs.flow_control.ui.controller.form;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.service.VehicleService;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


@Controller
public class VehicleFormController {
    @Autowired
    private VehicleService vehicleService;

    private Runnable onDataChanged;

    @FXML
    private TextField numberPlateField;
     
    @FXML
    private TextField modelField;

    @FXML
    private void onSave() {
        if (numberPlateField.getText().isBlank() || modelField.getText().isBlank()) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                    "Por favor preencha a placa e o modelo!");
            return;
        }
        try {
            vehicleService.saveVehicle(numberPlateField.getText(), modelField.getText());

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
                "Veículo cadastrado com sucesso!");
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
        Stage stage = (Stage) numberPlateField.getScene().getWindow();
        stage.close();
    }
}
