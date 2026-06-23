package br.com.midnightsyslabs.flow_control.ui.controller.form.edit;

import java.util.Comparator;
import java.util.function.UnaryOperator;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.service.ProductService;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
import br.com.midnightsyslabs.flow_control.view.ProductView;
import br.com.midnightsyslabs.flow_control.exception.ClientNotFoundException;
import br.com.midnightsyslabs.flow_control.exception.ProductNotFoundException;
import br.com.midnightsyslabs.flow_control.domain.entity.product.ProductPrice;
import br.com.midnightsyslabs.flow_control.repository.product.ProductRepository;
import br.com.midnightsyslabs.flow_control.domain.entity.product.MeasurementUnit;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

@Controller
public class ProductEditFormController {
    private ProductView productDTO;

    private final ProductService productService;

    private final ProductRepository productRepository;

    private MeasurementUnit selectedMeasurementUnit;

    private boolean loadingData = false;

    @FXML
    private TextField nameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField priceField;

    @FXML
    private Label lblQuantity;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField measurementUnitField;

    public ProductEditFormController(
            ProductService productService,
            ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;

    }

    @FXML
    public void initialize() {

        loadProductData();
        UiUtils.configurePriceField(priceField);
    }

    public void loadProductData() {

        if (productDTO == null) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro", "Dados incogruentes do produto.");
            return;
        }

        loadingData = true;

        productRepository.findByIdWithPriceHistory(productDTO.getId()).ifPresentOrElse(product -> {

            fillFields(
                    product.getName(),
                    product.getDescription(),
                    product.getCategory().getName(),
                    product.getProductPriceHistory().stream()
                            .max(Comparator.comparing(ProductPrice::getPriceChangeDate)).orElse(null).getPrice()
                            .toString(),
                    product.getMeasurementUnit().getUnit(),
                    product.getQuantity().toString(),
                    product.getMeasurementUnit());

        }, ClientNotFoundException::new);

        loadingData = false;
    }

    private void fillFields(String name, String description, String category, String price, String quantityUnit,
            String quantity, MeasurementUnit measurementUnit) {
        nameField.setText(name);
        descriptionField.setText(description);
        categoryField.setText(category);
        lblQuantity.setText(quantityUnit);
        quantityField.setText(quantity);
        priceField.setText(price.replace(".", ","));
        measurementUnitField.setText(measurementUnit.getName() + " (" + measurementUnit.getSymbol() + ")");
    }

    public void editProductForm(ProductView product) {
        this.productDTO = product;
    }

    @FXML
    public void onEdit() {
        try {

            if (nameField.getText().isEmpty() || descriptionField.getText().isEmpty()
                    || priceField.getText().isEmpty()) {
                UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                        "Por favor, preencha o nome, descrição, preço, selecione um tipo de unidade.");
                return;
            }
            productRepository.findByIdWithPriceHistory(productDTO.getId()).ifPresentOrElse(product -> {
                productService.editProduct(
                        product,
                        nameField.getText(),
                        descriptionField.getText(),
                        priceField.getText());

            }, ProductNotFoundException::new);

        } catch (IllegalArgumentException e) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Dados Inválidos", e.getMessage());
            return;
        }

        catch (DataIntegrityViolationException e) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro de Integridade de Dados",
                    "Algum dado único já existe no banco de dados");
            return;
        }

        catch (ProductNotFoundException e) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Produto não encontrado",
                    e.getMessage());
            return;
        }

        catch (Exception e) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro ao atualizar produto",
                    "Ocorreu um erro ao tentar atualizar o produto: " + e.getMessage());
            return;
        }

        close();
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
