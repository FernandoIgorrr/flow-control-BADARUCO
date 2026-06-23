package br.com.midnightsyslabs.flow_control.ui.controller.form;

import java.time.LocalDate;
import java.util.function.UnaryOperator;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.view.SupplierView;
import br.com.midnightsyslabs.flow_control.service.PurchaseService;
import br.com.midnightsyslabs.flow_control.service.SupplierService;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
import br.com.midnightsyslabs.flow_control.service.RawMaterialService;
import br.com.midnightsyslabs.flow_control.exception.SupplierNotFoundException;
import br.com.midnightsyslabs.flow_control.repository.partner.PartnerRepository;
import br.com.midnightsyslabs.flow_control.domain.entity.product.MeasurementUnit;
import br.com.midnightsyslabs.flow_control.domain.entity.raw_material.RawMaterial;
import br.com.midnightsyslabs.flow_control.exception.RawMaterialNotFoundException;
import br.com.midnightsyslabs.flow_control.exception.IllegalEmailArgumentException;
import br.com.midnightsyslabs.flow_control.repository.product.MeasurementUnitRepository;

import javafx.fxml.FXML;

import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextFormatter;

import javafx.util.StringConverter;

@Controller
public class PurchaseSupplierFormController {

    private final PurchaseService purchaseService;

    private final RawMaterialService rawMaterialService;

    private final MeasurementUnitRepository measurementUnitRepository;

    private final PartnerRepository partnerRepository;

    private SupplierView supplierView;

    private RawMaterial rawMaterialLeite;

    private Runnable onDataChanged;

    @FXML
    private TextField supplierField;

    @FXML
    private TextField rawMaterialField;

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

    PurchaseSupplierFormController(
            PurchaseService purchaseService,
            RawMaterialService rawMaterialService,
            MeasurementUnitRepository measurementUnitRepository,
            PartnerRepository partnerRepository) {
        this.purchaseService = purchaseService;
        this.rawMaterialService = rawMaterialService;
        this.measurementUnitRepository = measurementUnitRepository;
        this.partnerRepository = partnerRepository;
    }

    @FXML
    public void initialize() {
        configureMeasurementUnitComboBox();

        UiUtils.configurePriceField(priceField);
        UiUtils.configureQuantityField(quantityField);
        datePicker.setValue(LocalDate.now());
        datePicker.setEditable(false);
    }

    public void setSupplier(SupplierView supplierView) {
        this.supplierView = supplierView;

        supplierField.setText(supplierView.getName());
        try {
            rawMaterialLeite = rawMaterialService.getRawMaterialByName("Leite");
            rawMaterialField.setText(rawMaterialLeite.getName());
        } catch (RawMaterialNotFoundException e) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Dado não encontrado",
                    "A matéria prima / Insumo com nome de \"Leite\" não foi encontrado!");
            return;
        } catch (Exception e) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Algo deu errado!", e.getMessage());
            return;
        }
    }

    private void configureMeasurementUnitComboBox() {

        var measurementUnits = measurementUnitRepository.findAll();

        measurementUnitComboBox.getItems().setAll(measurementUnits);

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
                lblQuantity.setText(newValue.getUnit() + " em " + newValue.getPluralName() + " *");
                txtPriceTitle.setText(newValue.getName() + " (R$) *");
            }
        });

        if (!measurementUnits.isEmpty()) {
            measurementUnitComboBox.getSelectionModel().select(1);
        }
    }

    @FXML
    public void onSave() {

        try {

            if (priceField.getText().isEmpty()
                    || quantityField.getText().isEmpty() || measurementUnitComboBox.getValue() == null
                    || datePicker.getValue() == null) {
                UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                        "Por favor, preencha todas os campos!.");
                return;
            }

            partnerRepository.findById(this.supplierView.getId()).ifPresentOrElse(partner -> {
                purchaseService.savePurchase(partner,
                        rawMaterialLeite,
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
        Stage stage = (Stage) supplierField.getScene().getWindow();
        stage.close();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }
}
