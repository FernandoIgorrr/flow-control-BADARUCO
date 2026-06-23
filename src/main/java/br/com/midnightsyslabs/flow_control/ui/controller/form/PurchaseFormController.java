package br.com.midnightsyslabs.flow_control.ui.controller.form;

import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
import br.com.midnightsyslabs.flow_control.view.SupplierView;
import br.com.midnightsyslabs.flow_control.service.SupplierService;
import br.com.midnightsyslabs.flow_control.service.PurchaseService;
import br.com.midnightsyslabs.flow_control.service.RawMaterialService;
import br.com.midnightsyslabs.flow_control.exception.SupplierNotFoundException;
import br.com.midnightsyslabs.flow_control.repository.partner.PartnerRepository;
import br.com.midnightsyslabs.flow_control.domain.entity.product.MeasurementUnit;
import br.com.midnightsyslabs.flow_control.domain.entity.raw_material.RawMaterial;
import br.com.midnightsyslabs.flow_control.exception.IllegalEmailArgumentException;
import br.com.midnightsyslabs.flow_control.repository.product.MeasurementUnitRepository;

import javafx.fxml.FXML;
import javafx.util.StringConverter;

import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;

@Controller
public class PurchaseFormController {

    private final PurchaseService purchaseService;

    private final SupplierService supplierService;

    private final RawMaterialService rawMaterialService;

    private final MeasurementUnitRepository measurementUnitRepository;

    private final PartnerRepository partnerRepository;

    private Runnable onDataChanged;

    @FXML
    private ComboBox<SupplierView> supplierComboBox;

    @FXML
    private ComboBox<RawMaterial> rawMaterialComboBox;

    @FXML
    private Label lblQuantity;

    @FXML
    private TextField quantityField;

    @FXML
    private ComboBox<MeasurementUnit> measurementUnitComboBox;

    @FXML
    private TextFlow textFlowPrice;

    @FXML
    private Text txtPriceTitle;

    @FXML
    private TextField priceField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField noteField;

    PurchaseFormController(
            PurchaseService purchaseService,
            SupplierService supplierService,
            RawMaterialService rawMaterialService,
            MeasurementUnitRepository measurementUnitRepository,
            PartnerRepository partnerRepository) {
        this.purchaseService = purchaseService;
        this.supplierService = supplierService;
        this.rawMaterialService = rawMaterialService;
        this.measurementUnitRepository = measurementUnitRepository;
        this.partnerRepository = partnerRepository;
    }

    @FXML
    public void initialize() {

        configureSupplierComboBox();
        configureRawMaterialComboBox();
        configureMeasurementUnitComboBox();

        UiUtils.configurePriceField(priceField);
        UiUtils.configureQuantityField(quantityField);
        datePicker.setValue(LocalDate.now());
        datePicker.setEditable(false);
    }

    public void configureSupplierComboBox() {
        var suppliers = supplierService.getSuppliers();

        supplierComboBox.getItems().setAll(suppliers);

        supplierComboBox.setConverter(new StringConverter<SupplierView>() {

            @Override
            public String toString(SupplierView supplierDTO) {
                return supplierDTO == null
                        ? ""
                        : supplierDTO.getName();
            }

            @Override
            public SupplierView fromString(String string) {
                return null;
            }
        });

        if (!suppliers.isEmpty()) {
            supplierComboBox.getSelectionModel().selectFirst();
        }
    }

    public void configureRawMaterialComboBox() {
        var rawMaterial = rawMaterialService.getRawMaterialById((short) 1);

        if (rawMaterial.isEmpty()) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro ao encontrar Matéria-prima ID: 1",
                    "");
            close();
            return;
        }

        rawMaterialComboBox.getItems().setAll(rawMaterial.get());

        rawMaterialComboBox.setConverter(new StringConverter<RawMaterial>() {

            @Override
            public String toString(RawMaterial rawMaterial) {
                return rawMaterial == null
                        ? ""
                        : rawMaterial.getName();
            }

            @Override
            public RawMaterial fromString(String string) {
                return null;
            }
        });
        rawMaterialComboBox.getSelectionModel().selectFirst();
        rawMaterialComboBox.setDisable(true);
    }

    private void configureMeasurementUnitComboBox() {

        var measurementUnits = measurementUnitRepository.findById((short) 2);

        if (measurementUnits.isEmpty()) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro ao encontrar unidade em Litros ID: 2",
                    "");
            close();
            return;
        }

        measurementUnitComboBox.getItems().setAll(List.of(measurementUnits.get()));

        measurementUnitComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(MeasurementUnit unit) {
                return unit == null
                        ? ""
                        : unit.getName() + " (" + unit.getSymbol() + ")";
            }

            @Override
            public MeasurementUnit fromString(String string) {
                return null;
            }
        });

        measurementUnitComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                lblQuantity.setText(newValue.getUnit() + " *");
                txtPriceTitle.setText(newValue.getName() + " (R$)*");
            }
        });

        if (!measurementUnits.isEmpty()) {
            measurementUnitComboBox.getSelectionModel().selectFirst();
        }

        measurementUnitComboBox.setDisable(true);
    }

    @FXML
    public void onSave() {

        try {

            if (supplierComboBox.getValue() == null || rawMaterialComboBox.getValue() == null
                    || priceField.getText().isEmpty()
                    || quantityField.getText().isEmpty() || measurementUnitComboBox.getValue() == null
                    || datePicker.getValue() == null) {
                UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                        "Por favor, preencha todas os campos!.");
                return;
            }

            partnerRepository.findById(supplierComboBox.getValue().getId()).ifPresentOrElse(partner -> {
                purchaseService.savePurchase(partner,
                        rawMaterialComboBox.getValue(),
                        quantityField.getText(),
                        measurementUnitComboBox.getValue(),
                        priceField.getText(),
                        datePicker.getValue(),
                        noteField.getText());
            }, SupplierNotFoundException::new);

            if (onDataChanged != null) {
                onDataChanged.run();
            }

        } catch (IllegalEmailArgumentException e) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Erro de email", e.getMessage());
            return;
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
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro ao cadastrar produto",
                    "Ocorreu um erro ao tentar cadastrar o produto: " + e.getMessage());
            return;
        }

        close();

        UiUtils.showLabelAlert(Alert.AlertType.INFORMATION, "SUCESSO",
                "Compra cadastrada com sucesso!");
    }

    @FXML
    public void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) supplierComboBox.getScene().getWindow();
        stage.close();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }
}
