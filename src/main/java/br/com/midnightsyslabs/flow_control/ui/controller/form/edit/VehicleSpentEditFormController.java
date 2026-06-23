package br.com.midnightsyslabs.flow_control.ui.controller.form.edit;

import java.time.LocalDate;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.domain.entity.spent.SpentCategory;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.VehicleSpent;
import br.com.midnightsyslabs.flow_control.domain.entity.vehicle.Vehicle;
import br.com.midnightsyslabs.flow_control.repository.spent.SpentCategoryRepository;
import br.com.midnightsyslabs.flow_control.service.VehicleSpentService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import br.com.midnightsyslabs.flow_control.service.VehicleService;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;

import javafx.fxml.FXML;
import javafx.util.StringConverter;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

@Controller
public class VehicleSpentEditFormController {

    private final VehicleSpentService vehicleSpentService;
    private final VehicleService vehicleService;
    private final SpentCategoryRepository spentCategoryRepository;

    private Runnable onDataChanged;
    private boolean loadingData = false;
    private VehicleSpent vehicleSpent;
    private Integer vehicleSpentId;

    @FXML
    private ComboBox<Vehicle> vehicleComboBox;

    @FXML
    private ComboBox<SpentCategory> categoryComboBox;

    @FXML
    private TextField priceField;

    @FXML
    private TextField quantityField;

    @FXML
    private HBox quantityFieldContainer;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField noteField;

    public VehicleSpentEditFormController(
            VehicleSpentService vehicleSpentService,
            VehicleService vehicleService,
            SpentCategoryRepository spentCategoryRepository) {
        this.vehicleSpentService = vehicleSpentService;
        this.vehicleService = vehicleService;
        this.spentCategoryRepository = spentCategoryRepository;
    }

    @FXML
    public void initialize() {
        configureVehicleComboBox();
        configureCategoryComboBox();

        UiUtils.configurePriceField(priceField);
        UiUtils.configureQuantityField(quantityField);
        datePicker.setEditable(false);

        // Bloqueia o ComboBox de categoria para que o usuário não consiga alterá-la
        categoryComboBox.setDisable(true);

        // Listener para gerenciar a exibição do campo de quantidade com base na categoria carregada
        categoryComboBox.valueProperty().addListener((obs, oldCat, newCat) -> handleCategoryChange(newCat));

        loadVehicleSpentData();
    }

    public void loadVehicleSpentData() {
        if (vehicleSpentId == null) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro", "Dados incongruentes do gasto.");
            return;
        }

        loadingData = true;

        vehicleSpentService.findById(vehicleSpentId).ifPresentOrElse(spent -> {
            this.vehicleSpent = spent;

            // Seleciona o veículo original no ComboBox
            vehicleComboBox.getItems().stream()
                    .filter(v -> v.getNumberPlate().equals(spent.getVehicle().getNumberPlate()))
                    .findFirst()
                    .ifPresent(vehicleComboBox.getSelectionModel()::select);

            // Seleciona a categoria original no ComboBox (ficará apenas visual)
            categoryComboBox.getItems().stream()
                    .filter(c -> c.getId().equals(spent.getCategory().getId()))
                    .findFirst()
                    .ifPresent(categoryComboBox.getSelectionModel()::select);

            fillFields(
                    UtilsService.solveDot(spent.getQuantity().toString()),
                    UtilsService.solveDot(spent.getAmountPaid().toString()),
                    spent.getNote(),
                    spent.getDate()
            );

        }, () -> UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro", "Gasto não encontrado no sistema."));

        loadingData = false;
    }

    public void configureVehicleComboBox() {
        var vehicles = vehicleService.getVehicles();
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
    }

    public void configureCategoryComboBox() {
        var categories = spentCategoryRepository.findAll();
        categoryComboBox.getItems().setAll(categories);

        categoryComboBox.setConverter(new StringConverter<SpentCategory>() {
            @Override
            public String toString(SpentCategory category) {
                return category == null ? "" : category.getName();
            }

            @Override
            public SpentCategory fromString(String string) {
                return null;
            }
        });
    }

    private void handleCategoryChange(SpentCategory category) {
        if (category == null || quantityFieldContainer == null) return;
        
        // Se for Manutenção (ID 11), esconde visualmente o campo de quantidade
        boolean isMaintenance = category.getId() == 11;
        quantityFieldContainer.setVisible(!isMaintenance);
        quantityFieldContainer.setManaged(!isMaintenance);
    }

    private void fillFields(String quantity, String price, String note, LocalDate date) {
        quantityField.setText(quantity);
        priceField.setText(price);
        noteField.setText(note);
        datePicker.setValue(date);
    }

    public void editVehicleSpentForm(VehicleSpent spent) {
        this.vehicleSpentId = spent.getId();
    }

    @FXML
    public void onEdit() {
        try {
            SpentCategory selectedCategory = categoryComboBox.getValue();
            Vehicle selectedVehicle = vehicleComboBox.getValue();

            if (priceField.getText().isEmpty() || datePicker.getValue() == null 
                    || selectedCategory == null || selectedVehicle == null) {
                UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                        "Por favor, preencha todos os campos obrigatórios!.");
                return;
            }

            // Tratamento da quantidade fixa baseada na categoria
            boolean isMaintenance = selectedCategory.getId() == 11;
            if (!isMaintenance && quantityField.getText().isEmpty()) {
                UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                        "Por favor, preencha a quantidade.");
                return;
            }

            String finalQuantity = isMaintenance ? "1" : quantityField.getText();

            vehicleSpentService.updateVehicleSpent(
                    vehicleSpent,
                    priceField.getText(),
                    finalQuantity,
                    selectedCategory,
                    selectedVehicle,
                    datePicker.getValue(),
                    noteField.getText()
            );

            if (onDataChanged != null) {
                onDataChanged.run();
            }

        } catch (IllegalArgumentException e) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Dados Inválidos", e.getMessage());
            return;
        } catch (DataIntegrityViolationException e) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro de Integridade", "Erro ao atualizar dados no banco.");
            return;
        } catch (Exception e) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro ao atualizar",
                    "Ocorreu um erro inesperado: " + e.getMessage());
            return;
        }

        close();

        UiUtils.showLabelAlert(Alert.AlertType.INFORMATION, "SUCESSO",
                "Gasto de veículo atualizado com sucesso!");
    }

    @FXML
    public void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) vehicleComboBox.getScene().getWindow();
        stage.close();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }
}