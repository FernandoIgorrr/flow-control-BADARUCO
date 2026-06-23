package br.com.midnightsyslabs.flow_control.ui.controller.form;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.domain.entity.product.MeasurementUnit;
import br.com.midnightsyslabs.flow_control.domain.entity.product.Product;
import br.com.midnightsyslabs.flow_control.domain.entity.purchase.Purchase;
import br.com.midnightsyslabs.flow_control.repository.product.MeasurementUnitRepository;
import br.com.midnightsyslabs.flow_control.service.ProductService;
import br.com.midnightsyslabs.flow_control.service.ProductionService;
import br.com.midnightsyslabs.flow_control.service.PurchaseService;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
import jakarta.validation.ConstraintViolationException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

@Controller
public class ProductionFormController {

    @Autowired
    private MeasurementUnitRepository measurementUnitRepository;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductionService productionService;

    private Runnable onDataChanged;
/* 
    private List<PurchaseView> purchaseViews; */

    @FXML
    private ContextMenu purchasesSuggestions;

    /*
     * @FXML
     * private VBox purchaseFieldsBox;
     */
/* 
    private List<PurchaseRow> purchaseRows; */

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

    /*
     * @FXML
     * private Button btnAddPurchase;
     */

    @FXML
    public void initialize() {
        /* purchaseViews = purchaseService.getPurchasesView();
        purchaseRows = new ArrayList<>();
        purchasesSuggestions = new ContextMenu(); */

        configureRawMaterialPurchaseDateComboBox();
        // addPurchaseField();
        configureProductComboBox();
        UiUtils.configureQuantityField(this.quantityUsedField);
        UiUtils.configureQuantityField(this.grossQuantityProducedField);
        configureMeasurementUnitComboBox();
        UiUtils.configureQuantityField(this.quantityProducedField);

        configureMeasurementUnitComboBox();

        datePicker.setValue(LocalDate.now());
        datePicker.setEditable(false);
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

    /*
     * @FXML
     * private void addPurchaseField() {
     * 
     * VBox purchaseItemBox = new VBox(5);
     * purchaseItemBox.getStyleClass().add("purchase-item");
     * 
     * HBox headerBox = new HBox(10);
     * headerBox.setAlignment(Pos.BOTTOM_LEFT);
     * Label lblPurchaseMenuItem = new Label("ID da compra da matéria-prima *");
     * 
     * Label lblPurchaseQuantityUsed = new
     * Label("Preencha a quantidade usada da matéria-prima *");
     * 
     * TextField purchaseField = new TextField();
     * purchaseField.setPromptText("Digite o ID da compra...");
     * 
     * TextField purchaseQuantityUsedField = new TextField();
     * purchaseQuantityUsedField.setPromptText("0,0");
     * 
     * //ContextMenu suggestions = new ContextMenu();
     * //setupPurchaseAutocomplete(purchaseField, suggestions);
     * UiUtils.configureQuantityField(purchaseQuantityUsedField);
     * 
     * // spacer
     * Region spacer = new Region();
     * HBox.setHgrow(spacer, Priority.ALWAYS);
     * 
     * if (!purchaseFieldsBox.getChildren().isEmpty()) {
     * Button btnRemove = new Button();
     * btnRemove.getStyleClass().add("btn-action-delete");
     * 
     * StackPane pane = new StackPane();
     * 
     * pane.prefWidth(32);
     * pane.prefHeight(32);
     * 
     * ImageView iconRemove = new ImageView(
     * new
     * Image(getClass().getResourceAsStream("/images/carbon--close-outline.png")));
     * 
     * iconRemove.setFitWidth(32);
     * iconRemove.setFitHeight(32);
     * iconRemove.setPreserveRatio(true);
     * iconRemove.getStyleClass().add("icon-delete");
     * 
     * pane.getChildren().add(iconRemove);
     * 
     * btnRemove.getStyleClass().add("btn-action-add-purchase");
     * 
     * btnRemove.setGraphic(pane);
     * 
     * btnRemove.setOnAction(event -> {
     * purchaseFieldsBox.getChildren().remove(purchaseItemBox);
     * purchaseRows.removeIf(row -> row.purchaseQuantityUsedField ==
     * purchaseQuantityUsedField);
     * });
     * headerBox.getChildren().addAll(lblPurchaseMenuItem, spacer, btnRemove);
     * } else {
     * headerBox.getChildren().addAll(lblPurchaseMenuItem);
     * }
     * purchaseItemBox.getChildren().addAll(
     * headerBox,
     * purchaseField,
     * lblPurchaseQuantityUsed,
     * purchaseQuantityUsedField);
     * 
     * purchaseFieldsBox.getChildren().add(purchaseItemBox);
     * 
     * purchaseField.textProperty().addListener((obs, oldText, newText) -> {
     * 
     * if (newText == null || newText.length() < 1) {
     * purchasesSuggestions.hide();
     * return;
     * }
     * 
     * List<MenuItem> suggestionss = purchaseViews.stream()
     * .filter(p -> p.getId().toString().contains(newText))
     * .limit(10)
     * .map(purchaseView -> {
     * 
     * MenuItem item = new MenuItem(
     * "#" + purchaseView.getId() +
     * " | " + UtilsService.formatQuantity(purchaseView.getQuantity()) +
     * " " + purchaseView.getMeasurementUnitPluralName() +
     * " de " + purchaseView.getRawMaterialName() +
     * " ( " + UtilsService.formatPrice(purchaseView.getExpense()) + ")" +
     * " ~ " + purchaseView.getPartnerName());
     * 
     * item.setOnAction(e -> {
     * purchaseField.setText(item.getText());
     * purchasesSuggestions.hide();
     * purchaseRows.removeIf(row -> row.purchaseQuantityUsedField ==
     * purchaseQuantityUsedField);
     * purchaseRows.add(new PurchaseRow(purchaseView,purchaseQuantityUsedField));
     * 
     * });
     * 
     * return item;
     * })
     * .toList();
     * 
     * if (suggestionss.isEmpty()) {
     * purchasesSuggestions.hide();
     * } else {
     * purchasesSuggestions.getItems().setAll(suggestionss);
     * purchasesSuggestions.show(purchaseField, javafx.geometry.Side.BOTTOM, 0, 0);
     * }
     * });
     * }
     */

    /*
     * @FXML
     * private void addPurchaseField() {
     * 
     * boolean isFirst = purchaseFieldsBox.getChildren().isEmpty();
     * 
     * VBox purchaseItemBox = new VBox(5);
     * purchaseItemBox.getStyleClass().add("purchase-item");
     * 
     * // Header
     * HBox headerBox = new HBox(10);
     * headerBox.setAlignment(Pos.CENTER_LEFT);
     * 
     * Label lblPurchase = new Label("Código da compra da matéria-prima *");
     * 
     * TextField purchaseField = new TextField();
     * purchaseField.setPromptText("Digite o ID da compra...");
     * 
     * Label lblQuantityUsed = new Label("Digite a quantidade usada");
     * 
     * TextField quantityField = new TextField();
     * 
     * quantityField.setPromptText("0,0");
     * 
     * ContextMenu suggestions = new ContextMenu();
     * setupPurchaseAutocomplete(purchaseField, quantityField, suggestions);
     * configureQuantityField(quantityField);
     * 
     * Region spacer = new Region();
     * HBox.setHgrow(spacer, Priority.ALWAYS);
     * 
     * // Só cria o botão se NÃO for o primeiro bloco
     * if (!isFirst) {
     * Button btnRemove = new Button("✖");
     * btnRemove.setFocusTraversable(false);
     * btnRemove.setStyle("""
     * -fx-background-color: transparent;
     * -fx-text-fill: red;
     * -fx-font-size: 14px;
     * """);
     * 
     * btnRemove.setOnAction(e -> {
     * purchaseFieldsBox.getChildren().remove(purchaseItemBox);
     * // Remove da nossa lista de controle procurando pelo campo de quantidade
     * purchaseRows.removeIf(row -> row.quantityField == quantityField);
     * });
     * headerBox.getChildren().addAll(lblPurchase, spacer, btnRemove);
     * } else {
     * headerBox.getChildren().add(lblPurchase);
     * }
     * 
     * purchaseItemBox.getChildren().addAll(
     * headerBox,
     * purchaseField,
     * lblQuantityUsed,
     * quantityField);
     * 
     * purchaseFieldsBox.getChildren().add(purchaseItemBox);
     * }
     */

    /*
     * private void setupPurchaseAutocomplete(
     * TextField purchaseField,
     * 
     * ContextMenu purchasesSuggestions) {
     * var purchasesView = purchaseService.getPurchasesView();
     * 
     * purchaseField.textProperty().addListener((obs, oldText, newText) -> {
     * 
     * if (newText == null || newText.length() < 1) {
     * purchasesSuggestions.hide();
     * return;
     * }
     * 
     * List<MenuItem> suggestions = purchasesView.stream()
     * .filter(p -> p.getId().toString().contains(newText))
     * .limit(10)
     * .map(purchaseView -> {
     * 
     * MenuItem item = new MenuItem(
     * "#" + purchaseView.getId() +
     * " | " + UtilsService.formatQuantity(purchaseView.getQuantity()) +
     * " " + purchaseView.getMeasurementUnitPluralName() +
     * " de " + purchaseView.getRawMaterialName() +
     * " ( " + UtilsService.formatPrice(purchaseView.getExpense()) + ")" +
     * " ~ " + purchaseView.getPartnerName());
     * 
     * item.setOnAction(e -> {
     * purchaseField.setText(item.getText());
     * purchasesSuggestions.hide();
     * purchaseRows.add(new PurchaseRow(purchaseView, quantityUsedField));
     * 
     * // aqui eu crio o novo campo automaticamente
     * addPurchaseField();
     * });
     * 
     * return item;
     * })
     * .toList();
     * 
     * if (suggestions.isEmpty()) {
     * purchasesSuggestions.hide();
     * } else {
     * purchasesSuggestions.getItems().setAll(suggestions);
     * purchasesSuggestions.show(purchaseField, javafx.geometry.Side.BOTTOM, 0, 0);
     * }
     * });
     * }
     */

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
    private void onSave() {
       /*  if (this.purchaseRows.isEmpty()) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Atenção", "Adicione pelo menos uma matéria prima.");
            return;
        } */

        try {
           /*  for (PurchaseRow row : this.purchaseRows) {
                String quantityFieldText = row.purchaseQuantityUsedField.getText().replace(",", "."); // Converte para
                                                                                                      // formato
                // decimal
                // Java
                if (quantityFieldText.isEmpty()) {
                    UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Atenção",
                            "Está faltando a quantidade usada em algum campo!.");
                    return;
                }

            } */

            if (this.grossQuantityProducedField.getText().isBlank() || this.quantityProducedField.getText().isBlank()
                    || this.productComboBox.getValue() == null || this.quantityUsedField.getText().isBlank()
                    || this.datePicker == null) {
                UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                        "Por favor  preencha o campo de quantidade usada, quantidade produzida do produto bruto e do refinado!");
                return;
            }

            productionService.saveProdction(
                    // this.purchaseRows,
                    this.quantityUsedField.getText(),
                    this.grossQuantityProducedField.getText(),
                    this.gqpMeasurementUnitComboBox.getValue(),
                    this.productComboBox.getValue(),
                    this.quantityProducedField.getText(),
                    this.datePicker.getValue(),
                    this.rawMaterialPurchaseDateComboBox.getValue());

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
                "Produção cadastrada com sucesso!");
    }

    @FXML
    private void onCancel() {
        close();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    private void close() {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }
}