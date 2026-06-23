package br.com.midnightsyslabs.flow_control.ui.controller.form;

import java.time.LocalDate;
import java.util.function.UnaryOperator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.domain.entity.spent.SpentCategory;
import br.com.midnightsyslabs.flow_control.domain.entity.vehicle.Vehicle;
import br.com.midnightsyslabs.flow_control.repository.spent.SpentCategoryRepository;
import br.com.midnightsyslabs.flow_control.repository.vehicle.VehicleSpentRepository;
import br.com.midnightsyslabs.flow_control.service.EmojiService;
import br.com.midnightsyslabs.flow_control.service.SpentService;
import br.com.midnightsyslabs.flow_control.service.VehicleService;
import br.com.midnightsyslabs.flow_control.service.VehicleSpentService;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

@Controller
public class SpentFormController {

    @Autowired
    private SpentCategoryRepository spentCategoryRepository;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleSpentService vehicleSpentService;

    @Autowired
    private EmojiService emojiService;

    @Autowired
    private SpentService spentService;

    private Runnable onDataChanged;

    @FXML
    private ComboBox<SpentCategory> spentCategoryComboBox;

    @FXML
    private TextField amountPaidField;

    @FXML
    private TextField spentDescriptionField;

    @FXML
    private DatePicker datePicker;

    // --- NOVOS COMPONENTES INJETADOS ---
    @FXML
    private VBox vehicleFieldContainer;

    @FXML
    private VBox quantityFieldContainer;

    @FXML
    private ComboBox<Vehicle> vehicleComboBox;

    @FXML
    private TextField quantityField;

    @FXML
    public void initialize() {
        configureSpentCategoryComboBox();
        UiUtils.configurePriceField(amountPaidField);
        UiUtils.configurePriceField(quantityField); // Se for numérico/decimal
        datePicker.setValue(LocalDate.now());
        datePicker.setEditable(false);

        // Listener para monitorar a troca de categorias
        spentCategoryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            handleCategoryChange(newVal);
        });

        // Inicializa a configuração dos veículos
        configureVehicleComboBox();
    }

    private void handleCategoryChange(SpentCategory category) {
    if (category == null) return;

    // Condições separadas por ID
    boolean isFuel = category.getId() == 10;        // Combustível
    boolean isMaintenance = category.getId() == 11; // Manutenção

    // O container do Veículo aparece TANTO para combustível QUANTO para manutenção
    boolean showVehicle = isFuel || isMaintenance;
    vehicleFieldContainer.setVisible(showVehicle);
    vehicleFieldContainer.setManaged(showVehicle);
    
    // O container de Quantidade só aparece para Combustível (ID 10)
    quantityFieldContainer.setVisible(isFuel);
    quantityFieldContainer.setManaged(isFuel);
}

    private void configureVehicleComboBox() {
        // Busca apenas os veículos ativos/não deletados
        var vehicles = vehicleService.getVehicles().stream()
                .filter(v -> v.getDeletedAt() == null).toList();

        vehicleComboBox.getItems().setAll(vehicles);
        vehicleComboBox.setConverter(new StringConverter<Vehicle>() {
            @Override
            public String toString(Vehicle vehicle) {
                return vehicle == null ? "" : vehicle.getNumberPlate() + " - " + vehicle.getModel();
            }

            @Override
            public Vehicle fromString(String string) {
                return null;
            }
        });

        if (!vehicles.isEmpty()) {
            vehicleComboBox.getSelectionModel().selectFirst();
        }
    }

    public void configureSpentCategoryComboBox() {
        var spentCategories = spentCategoryRepository.findAll();

        spentCategories = spentCategories.stream()
                .filter(sc -> sc.getId() > 2).toList();

        spentCategoryComboBox.getItems().setAll(spentCategories);
        spentCategoryComboBox.setConverter(new StringConverter<SpentCategory>() {

            @Override
            public String toString(SpentCategory spentCategory) {
                return spentCategory == null ? ""
                        : emojiService.getEmoji(spentCategory.getId()) + " " + spentCategory.getName();
            }

            @Override
            public SpentCategory fromString(String string) {
                return null;
            }

        });

        if (!spentCategories.isEmpty()) {
            spentCategoryComboBox.getSelectionModel().selectFirst();
        }

    }

    @FXML
public void onSave() {
    if (amountPaidField.getText().isBlank()) {
        UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Atenção", "A quantia paga está vazia");
        return;
    }

    SpentCategory selectedCategory = spentCategoryComboBox.getValue();
    if (selectedCategory == null) return;

    boolean isFuel = selectedCategory.getId() == 10;
    boolean isMaintenance = selectedCategory.getId() == 11;
    boolean isVehicleSpent = isFuel || isMaintenance;

    // Validações para gastos de veículo
    if (isVehicleSpent) {
        if (vehicleComboBox.getValue() == null) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Atenção", "Selecione um veículo.");
            return;
        }
        // Só valida o campo de texto se for combustível
        if (isFuel && quantityField.getText().isBlank()) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Atenção", "Por favor, informe a quantidade.");
            return;
        }
    }

    try {
        if (isVehicleSpent) {
            // Se for manutenção, define a string da quantidade como "1"
            String finalQuantity = isMaintenance ? "1" : quantityField.getText();

            vehicleSpentService.saveVehicleSpent(
                    amountPaidField.getText(),
                    finalQuantity,
                    selectedCategory,
                    vehicleComboBox.getValue(),
                    spentDescriptionField.getText(),
                    datePicker.getValue()
            );
        } else {
            spentService.saveSpent(
                    amountPaidField.getText(), 
                    selectedCategory,
                    spentDescriptionField.getText(), 
                    datePicker.getValue()
            );
        }

        if (onDataChanged != null) {
            onDataChanged.run();
        }
    } catch (Exception e) {
        UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Atenção", "Algo deu errado! " + e.getMessage());
        return;
    }

    close();

    UiUtils.showLabelAlert(Alert.AlertType.INFORMATION, "SUCESSO",
            isVehicleSpent ? "Despesa de veículo cadastrada com sucesso!" : "Despesa cadastrada com sucesso!");
}

    @FXML
    public void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) spentCategoryComboBox.getScene().getWindow();
        stage.close();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }
}
