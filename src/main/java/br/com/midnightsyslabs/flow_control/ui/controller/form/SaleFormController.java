package br.com.midnightsyslabs.flow_control.ui.controller.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.domain.entity.product.Product;
import br.com.midnightsyslabs.flow_control.exception.ClientNotFoundException;
import br.com.midnightsyslabs.flow_control.repository.partner.PartnerRepository;
import br.com.midnightsyslabs.flow_control.service.ClientService;
import br.com.midnightsyslabs.flow_control.service.ProductService;
import br.com.midnightsyslabs.flow_control.service.SaleService;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
import br.com.midnightsyslabs.flow_control.view.ClientView;
import jakarta.validation.ConstraintViolationException;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

@Controller
public class SaleFormController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private SaleService saleService;

    @Autowired
    private PartnerRepository partnerRepository;

    private List<ProductRow> productsRows;

    private Runnable onDataChanged;

    @FXML
    private ContextMenu clientsSuggestions;

    @FXML
    private ClientView selectedClient;

    @FXML
    private VBox productFieldsBox;

    @FXML
    private Button btnAddProduct;

    @FXML
    private TextField clientField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label lblRevenue;

    @FXML
    private void initialize() {
        clientsSuggestions = new ContextMenu();
        productsRows = new ArrayList<>();
        addProductFields();
        setupClientAutocomplete();
        datePicker.setValue(LocalDate.now());
        datePicker.setEditable(false);
    }

    @FXML
    private void addProductFields() {

        VBox productItemBox = new VBox(5);
        productItemBox.getStyleClass().add("purchase-item");

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.BOTTOM_LEFT);
        Label lblProductComboBox = new Label("Selecione o produto *");

        // espaçador
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblProductQuantitySold = new Label("Preencha a quantidade do produto *");

        /* LISTA DOS PRODUTOS */
        var products = productService.getProducts();
        ComboBox<Product> productComboBox = new ComboBox<Product>();
        productComboBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(productComboBox, Priority.ALWAYS);
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

        /* CAMPO PARA COLOCAR QUANTIDADE VENDIDA DO PRODUTO */
        TextField productQuantitySoldField = new TextField();
        UiUtils.configureQuantityField(productQuantitySoldField);

        if (!productFieldsBox.getChildren().isEmpty()) {
            Button btnRemove = new Button();
            btnRemove.getStyleClass().add("btn-action-delete");

            StackPane pane = new StackPane();

            pane.prefWidth(32);
            pane.prefHeight(32);

            ImageView iconRemove = new ImageView(
                    new Image(getClass().getResourceAsStream("/images/carbon--close-outline.png")));

            iconRemove.setFitWidth(32);
            iconRemove.setFitHeight(32);
            iconRemove.setPreserveRatio(true);
            iconRemove.getStyleClass().add("icon-delete");

            pane.getChildren().add(iconRemove);

            btnRemove.getStyleClass().add("btn-action-add-purchase");

            btnRemove.setGraphic(pane);

            btnRemove.setOnAction(event -> {
                productFieldsBox.getChildren().remove(productItemBox);
                productsRows.removeIf(row -> row.productQuantitySoldField == productQuantitySoldField);
            });
            headerBox.getChildren().addAll(lblProductComboBox, spacer, btnRemove);
        } else {
            headerBox.getChildren().addAll(lblProductComboBox);
        }
        productItemBox.getChildren().addAll(
                headerBox,
                productComboBox,
                lblProductQuantitySold,
                productQuantitySoldField);

        productFieldsBox.getChildren().add(productItemBox);

        productsRows.add(new ProductRow(productComboBox, productQuantitySoldField));
    }

  

    private void setupClientAutocomplete() {

        var clients = clientService.getClients();

        clientField.textProperty().addListener((obs, oldText, newText) -> {

            if (newText == null || newText.length() < 2) {
                clientsSuggestions.hide();
                return;
            }

            List<MenuItem> suggestions = clients.stream()
                    .filter(c -> c.getName().toLowerCase()
                            .contains(newText.toLowerCase()))
                    .limit(10)
                    .map(client -> {
                        MenuItem item = new MenuItem(client.getName());
                        item.setOnAction(e -> {
                            this.selectedClient = client;
                            clientField.setText(client.getName());
                            clientsSuggestions.hide();
                        });
                        return item;
                    })
                    .collect(Collectors.toList());

            if (suggestions.isEmpty()) {
                clientsSuggestions.hide();
            } else {
                clientsSuggestions.getItems().setAll(suggestions);
                if (!clientsSuggestions.isShowing()) {
                    clientsSuggestions.show(clientField,
                            javafx.geometry.Side.BOTTOM, 0, 0);
                }
            }
        });
    }

    @FXML
    public void onSave() {
        if (this.productsRows.isEmpty()) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Atenção", "Adicione pelo menos um produto.");
            return;
        }

        try {
            for (var row : this.productsRows) {
                String productQuantitySold = row.productQuantitySoldField.getText().replace(",", ".");

                if (productQuantitySold.isBlank()) {
                    UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Atenção",
                            "Está faltando a quantidade vendida em algum campo!.");
                    return;
                }

            }

            if (this.selectedClient == null
                    || this.datePicker == null) {
                UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                        "Por favor  preencha o campo referente ao cliente e a data");
                return;
            }

            partnerRepository.findById(this.selectedClient.getId()).ifPresentOrElse(partner -> {
                saleService.saveSale(productsRows, partner, datePicker.getValue());
            }, ClientNotFoundException::new);

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
                "Venda cadastrada com sucesso!");
    }

    @FXML
    public void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }
}
