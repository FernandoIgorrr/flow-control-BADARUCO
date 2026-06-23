package br.com.midnightsyslabs.flow_control.ui.controller.form.edit;

import java.time.LocalDate;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
import br.com.midnightsyslabs.flow_control.view.PurchaseView;
import br.com.midnightsyslabs.flow_control.view.SupplierView;
import br.com.midnightsyslabs.flow_control.service.SupplierService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import br.com.midnightsyslabs.flow_control.service.PurchaseService;
import br.com.midnightsyslabs.flow_control.exception.SupplierNotFoundException;
import br.com.midnightsyslabs.flow_control.repository.partner.PartnerRepository;
import br.com.midnightsyslabs.flow_control.domain.entity.purchase.Purchase;
import br.com.midnightsyslabs.flow_control.exception.IllegalEmailArgumentException;
import br.com.midnightsyslabs.flow_control.exception.PurchaseNotFoundException;

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
public class PurchaseEditFormController {

    private final PurchaseService purchaseService;

    private final SupplierService supplierService;

    private final PartnerRepository partnerRepository;

    private Runnable onDataChanged;

    private boolean loadingData = false;

    private PurchaseView purchaseView;

    private Purchase purchase;

    @FXML
    private ComboBox<SupplierView> supplierComboBox;

    @FXML
    private TextField rawMaterialField;

    @FXML
    private Label lblQuantity;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField measurementUnitField;

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

    public PurchaseEditFormController(
            PurchaseService purchaseService,
            SupplierService supplierService,
            PartnerRepository partnerRepository) {
        this.purchaseService = purchaseService;
        this.supplierService = supplierService;
        this.partnerRepository = partnerRepository;
    }

    @FXML
    public void initialize() {

        configureSupplierComboBox();

        UiUtils.configurePriceField(priceField);
        UiUtils.configureQuantityField(quantityField);
        // datePicker.setValue(LocalDate.now());
        datePicker.setEditable(false);

        loadPurchaseData();
    }

    public void loadPurchaseData() {

        if (purchaseView == null) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro", "Dados incogruentes da compra.");
            return;
        }

        loadingData = true;

        purchaseService.findById(purchaseView.getId()).ifPresentOrElse(purchase -> {
            this.purchase = purchase;
            supplierComboBox.getItems()
                    .stream()
                    .filter(s -> s.getId().equals(purchase.getPartner().getId()))
                    .findFirst()
                    .ifPresent(supplierComboBox.getSelectionModel()::select);

            fillFields(UtilsService.solveDot(purchase.getQuantity().toString()),
                    UtilsService.solveDot(purchase.getPricePerUnit().toString()),
                    purchase.getMeasurementUnit().getUnit(),
                    purchase.getRawMaterial().getName(),
                    purchase.getMeasurementUnit().getName() + " " + "(" + purchase.getMeasurementUnit().getSymbol()
                            + ")",
                    purchase.getNote(),
                    purchase.getDate());

        }, PurchaseNotFoundException::new);

        loadingData = false;
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

    private void fillFields(String volume, String price, String meansurementUnitUnit, String rawMaterial,
            String meansurementUnitSymbol, String note, LocalDate date) {
        quantityField.setText(volume);
        priceField.setText(price);
        lblQuantity.setText(meansurementUnitUnit);
        rawMaterialField.setText(rawMaterial);
        measurementUnitField.setText(meansurementUnitSymbol);
        noteField.setText(note);
        datePicker.setValue(date);
    }

    public void editPurchaseForm(PurchaseView purchaseView) {
        this.purchaseView = purchaseView;
    }

    @FXML
    public void onEdit() {

        try {

            if (priceField.getText().isEmpty()
                    || quantityField.getText().isEmpty() || datePicker.getValue() == null) {
                UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                        "Por favor, preencha todas os campos!.");
                return;
            }

            partnerRepository.findById(supplierComboBox.getValue().getId()).ifPresentOrElse(partner -> {
                purchaseService.updatePurchase(
                        purchase,
                        partner,
                        quantityField.getText(),
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
                "Compra atualizada com sucesso!");
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
