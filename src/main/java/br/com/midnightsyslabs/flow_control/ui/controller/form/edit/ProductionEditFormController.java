package br.com.midnightsyslabs.flow_control.ui.controller.form.edit;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.domain.entity.product.MeasurementUnit;
import br.com.midnightsyslabs.flow_control.domain.entity.product.Product;
import br.com.midnightsyslabs.flow_control.domain.entity.production.Production;
import br.com.midnightsyslabs.flow_control.domain.entity.purchase.Purchase;
import br.com.midnightsyslabs.flow_control.exception.ProductionNotFoundException;
import br.com.midnightsyslabs.flow_control.repository.product.MeasurementUnitRepository;
import br.com.midnightsyslabs.flow_control.service.ProductService;
import br.com.midnightsyslabs.flow_control.service.ProductionService;
import br.com.midnightsyslabs.flow_control.service.PurchaseService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
import br.com.midnightsyslabs.flow_control.view.ProductionView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

@Controller
public class ProductionEditFormController {

    private MeasurementUnitRepository measurementUnitRepository;

    private PurchaseService purchaseService;

    private ProductService productService;

    private ProductionService productionService;

    private ProductionView productionView;

    private Production production;

    private Runnable onDataChanged;
    /*
     * private List<PurchaseView> purchaseViews;
     */

    @FXML
    private ContextMenu purchasesSuggestions;

    /*
     * @FXML
     * private VBox purchaseFieldsBox;
     */
    /*
     * private List<PurchaseRow> purchaseRows;
     */

    @FXML
    private ComboBox<Product> productComboBox;

    @FXML
    private Label lblGrossQuantityProduced;

    @FXML
    private TextField grossQuantityProducedField;

    @FXML
    private ComboBox<MeasurementUnit> gqpMeasurementUnitComboBox;

    @FXML
    private ComboBox<LocalDate> rawMaterialPurchaseDateComboBox;

    @FXML
    private TextField quantityProducedField;

    @FXML
    private TextField quantityUsedField;

    @FXML
    private DatePicker datePicker;

    public ProductionEditFormController(
            MeasurementUnitRepository measurementUnitRepository,
            PurchaseService purchaseService,
            ProductService productService,
            ProductionService productionService) {

        this.measurementUnitRepository = measurementUnitRepository;
        this.purchaseService = purchaseService;
        this.productService = productService;
        this.productionService = productionService;

    }

    @FXML
    public void initialize() {
        /*
         * purchaseViews = purchaseService.getPurchasesView();
         * purchaseRows = new ArrayList<>();
         * purchasesSuggestions = new ContextMenu();
         */

        configureRawMaterialPurchaseDateComboBox();
        // addPurchaseField();
        configureProductComboBox();
        UiUtils.configureQuantityField(this.quantityUsedField);
        UiUtils.configureQuantityField(this.grossQuantityProducedField);
        configureMeasurementUnitComboBox();
        UiUtils.configureQuantityField(this.quantityProducedField);

        configureMeasurementUnitComboBox();

        datePicker.setEditable(false);

        loadProductionData();
    }

    private void configureRawMaterialPurchaseDateComboBox() {

        List<LocalDate> dates = purchaseService.getPurchases().stream()
                .map(Purchase::getDate).distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        this.rawMaterialPurchaseDateComboBox.getItems().setAll(dates);

        this.rawMaterialPurchaseDateComboBox.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                        "dd 'de' MMMM 'de' yyyy",
                        Locale.forLanguageTag("pt-BR"));
                return date == null
                        ? ""
                        : date.format(formatter);
            }

            @Override
            public LocalDate fromString(String string) {
                return null;
            }
        });

        if (!dates.isEmpty()) {
            this.rawMaterialPurchaseDateComboBox.getSelectionModel().selectFirst();
        }
    }

    private void configureMeasurementUnitComboBox() {

        var measurementUnits = measurementUnitRepository.findAll();

        this.gqpMeasurementUnitComboBox.getItems().setAll(measurementUnits);

        this.gqpMeasurementUnitComboBox.setConverter(new StringConverter<>() {
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

        gqpMeasurementUnitComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {

                lblGrossQuantityProduced.setText("Bruto em (" + newValue.getSymbol() + ")" + " produzido *");
            }
        });

        if (!measurementUnits.isEmpty()) {
            this.gqpMeasurementUnitComboBox.getSelectionModel().selectFirst();
        }
    }

    public void loadProductionData() {

        if (productionView == null) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro", "Dados incogruentes da produção.");
            return;
        }

        productionService.findById(productionView.getId()).ifPresentOrElse(production -> {
            this.production = production;
            rawMaterialPurchaseDateComboBox.getItems()
                    .stream()
                    .filter(date -> date.equals(production.getRawMaterialPurchaseDate()))
                    .findFirst()
                    .ifPresent(rawMaterialPurchaseDateComboBox.getSelectionModel()::select);

            gqpMeasurementUnitComboBox.getItems()
                    .stream()
                    .filter(mu -> mu.equals(production.getGqpMeasurementUnit()))
                    .findFirst()
                    .ifPresent(gqpMeasurementUnitComboBox.getSelectionModel()::select);

            productComboBox.getItems()
                    .stream()
                    .filter(product -> product.equals(production.getProduct()))
                    .findFirst()
                    .ifPresent(productComboBox.getSelectionModel()::select);

            fillFields(UtilsService.solveDot(production.getQuantityUsed().toString()),
                    UtilsService.solveDot(production.getGrossQuantityProduced().toString()),
                    UtilsService.solveDot(production.getQuantityProduced().toString()),
                    production.getDate());

        }, ProductionNotFoundException::new);

    }

    private void fillFields(
            String quantityUsed,
            String quantityGrossProduced,
            String quantityProduced,
            LocalDate date) {

        quantityUsedField.setText(quantityUsed);
        grossQuantityProducedField.setText(quantityGrossProduced);
        quantityProducedField.setText(quantityProduced);
        datePicker.setValue(date);
    }

    public void editProductionForm(ProductionView productionView) {
        this.productionView = productionView;
    }

    private void configureProductComboBox() {
        var products = productService.getProducts();

        productComboBox.getItems().setAll(products);

        productComboBox.setConverter(new StringConverter<Product>() {

            @Override
            public String toString(Product product) {

                return product == null ? ""
                        : product.getName() + " [ " + product.getQuantity().toString().replace(".", ",") + " ("
                                + product.getMeasurementUnit().getSymbol() + ") ]";
            }

            @Override
            public Product fromString(String string) {
                return null;
            }

        });

        if (!products.isEmpty()) {
            productComboBox.getSelectionModel().selectFirst();
        }

    }

    @FXML
    private void onEdit() {

        try {

            if (this.grossQuantityProducedField.getText().isBlank() || this.quantityProducedField.getText().isBlank()
                    || this.productComboBox.getValue() == null || this.quantityUsedField.getText().isBlank()
                    || this.datePicker == null) {
                UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                        "Por favor  preencha o campo de quantidade usada, quantidade produzida do produto bruto e do refinado!");
                return;
            }

            productionService.updateProduction(
                    this.production,
                    this.quantityUsedField.getText(),
                    this.rawMaterialPurchaseDateComboBox.getValue(),
                    this.grossQuantityProducedField.getText(),
                    this.gqpMeasurementUnitComboBox.getValue(),
                    this.productComboBox.getValue(),
                    this.quantityProducedField.getText(),
                    this.datePicker.getValue()
                );

            if (onDataChanged != null) {
                onDataChanged.run();
            }
        } catch (ConstraintViolationException e) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Atenção", "Algum campo viola as regras de valores!");
            return;
        }

        catch (Exception e) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Atenção", "Algo deu errado!" + e.getCause());
            return;
        }

        close();

        UiUtils.showLabelAlert(Alert.AlertType.INFORMATION, "SUCESSO",
                "Produção alterada com sucesso!");
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }
}
