package br.com.midnightsyslabs.flow_control.ui.controller.form;

import org.springframework.stereotype.Controller;
import org.springframework.dao.DataIntegrityViolationException;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import br.com.midnightsyslabs.flow_control.service.RawMaterialService;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;

@Controller
public class RawMaterialFormController {

    private final RawMaterialService rawMaterialService;

    private Runnable onDataChanged;

    @FXML
    private TextField nameField;

    @FXML
    private TextField descriptionField;

    RawMaterialFormController(
            RawMaterialService rawMaterialService) {
        this.rawMaterialService = rawMaterialService;
    }

    @FXML
    public void onSave() {
        try {

            if (nameField.getText().isEmpty() || descriptionField.getText().isEmpty()) {
                UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                        "Por favor, preencha o nome e a descrição.");
                return;
            }

            rawMaterialService.saveRawMaterial(
                    nameField.getText(),
                    descriptionField.getText());

            if (onDataChanged != null) {
                onDataChanged.run();
            }

           

        }

        catch (IllegalArgumentException e) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Dados Inválidos", e.getMessage());
            return;
        }

        catch (DataIntegrityViolationException e) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro de Integridade de Dados",
                    "");
            return;
        } catch (Exception e) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro ao cadastrar Matéria-Prima",
                    "Ocorreu um erro ao tentar cadastrar o Matéria-Prima: " + e.getMessage());
            System.err.println(e.getMessage());
            return;
        }

        close();

          UiUtils.showLabelAlert(Alert.AlertType.INFORMATION, "SUCESSO",
                    "Insumo cadastrado com sucesso!");
    }

    @FXML
    private void onCancel() {
        close();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    private void close() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
