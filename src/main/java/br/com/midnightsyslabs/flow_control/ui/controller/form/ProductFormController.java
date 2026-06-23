package br.com.midnightsyslabs.flow_control.ui.controller.form;

import org.springframework.stereotype.Controller;

import java.util.function.UnaryOperator;

import org.springframework.dao.DataIntegrityViolationException;

import br.com.midnightsyslabs.flow_control.service.ProductService;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
import jakarta.validation.ConstraintViolationException;
import br.com.midnightsyslabs.flow_control.domain.entity.product.MeasurementUnit;
import br.com.midnightsyslabs.flow_control.domain.entity.product.ProductCategory;
import br.com.midnightsyslabs.flow_control.exception.IllegalEmailArgumentException;
import br.com.midnightsyslabs.flow_control.repository.product.MeasurementUnitRepository;
import br.com.midnightsyslabs.flow_control.repository.product.ProductCategoryRepository;
import javafx.fxml.FXML;

import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

@Controller
public class ProductFormController {

    private final ProductService productService;

    private final ProductCategoryRepository productCategoryRepository;

    private final MeasurementUnitRepository measurementUnitRepository;

    // private final ContextMenu citySuggestions;
    private MeasurementUnit selectedMeasurementUnit;

    private Runnable onDataChanged;

    @FXML
    private TextField nameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField priceField;

    @FXML
    private Label lblQuantity;

    @FXML
    private TextField quantityField;

    @FXML
    private ComboBox<ProductCategory> productCategoryComboBox;

    @FXML
    private ComboBox<MeasurementUnit> measurementUnitComboBox;

    public ProductFormController(
            ProductService productService,
            ProductCategoryRepository productCategoryRepository,
            MeasurementUnitRepository measurementUnitRepository) {
        this.productService = productService;
        this.productCategoryRepository = productCategoryRepository;
        this.measurementUnitRepository = measurementUnitRepository;

    }

    @FXML
    public void initialize() {

        configureProductCategoryComboBox();
        configureMeasurementUnitComboBox();
        UiUtils.configurePriceField(priceField);
        UiUtils.configureQuantityField(quantityField);
    }

    private void configureProductCategoryComboBox() {
        var productCategories = productCategoryRepository.findAll();

        productCategoryComboBox.getItems().setAll(productCategories);

        productCategoryComboBox.setConverter(new StringConverter<ProductCategory>() {
            @Override
            public String toString(ProductCategory category) {
                return category == null
                        ? ""
                        : category.getName();
            }

            @Override
            public ProductCategory fromString(String string) {
                return null;
            }
        });

        if (!productCategories.isEmpty()) {
            productCategoryComboBox.getSelectionModel().selectFirst();
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
                selectedMeasurementUnit = newValue;
                lblQuantity.setText(newValue.getUnit() + " *");
            }
        });

        if (!measurementUnits.isEmpty()) {
            measurementUnitComboBox.getSelectionModel().selectFirst();
        }
    }

    @FXML
    public void onSave() {
        try {

            if (nameField.getText().isEmpty() || descriptionField.getText().isEmpty() || priceField.getText().isEmpty()
                    || selectedMeasurementUnit == null || measurementUnitComboBox.getValue() == null) {
                UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                        "Por favor, preencha o nome, descrição, preço, selecione um tipo de unidade.");
                return;
            }

            productService.saveProduct(
                    nameField.getText(),
                    descriptionField.getText(),
                    productCategoryComboBox.getValue(),
                    priceField.getText(),
                    quantityField.getText(),
                    measurementUnitComboBox.getValue());

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
        } catch (ConstraintViolationException e) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Dados Inválidos",
                    "O dados númericos tem que ser maior que zero!");
            return;
        } catch (Exception e) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro ao cadastrar produto",
                    "Ocorreu um erro ao tentar cadastrar o produto: " + e.getMessage());
            System.err.println(e.getMessage());
            return;
        }

        close();

        UiUtils.showLabelAlert(Alert.AlertType.INFORMATION, "SUCESSO",
                "Produto cadastrado com sucesso!");

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
