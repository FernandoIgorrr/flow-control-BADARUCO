package br.com.midnightsyslabs.flow_control.ui.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.config.TimeIntervalEnum;
import br.com.midnightsyslabs.flow_control.domain.entity.raw_material.RawMaterial;
import br.com.midnightsyslabs.flow_control.dto.SaleDTO;
import br.com.midnightsyslabs.flow_control.service.DateService;
import br.com.midnightsyslabs.flow_control.service.PurchaseService;
import br.com.midnightsyslabs.flow_control.service.RawMaterialService;
import br.com.midnightsyslabs.flow_control.service.SupplierService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import br.com.midnightsyslabs.flow_control.ui.cards.PurchaseCard;
import br.com.midnightsyslabs.flow_control.ui.controller.form.PurchaseFormController;
import br.com.midnightsyslabs.flow_control.view.PurchaseView;
import br.com.midnightsyslabs.flow_control.view.SupplierView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;

@Controller
public class PurchasesController {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private RawMaterialService rawMaterialService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ApplicationContext context;

    private List<PurchaseView> allPurchasesView; // lista completa

    private List<PurchaseView> filteredPurchases; // resultado dos filtros

    @FXML
    private Button btnAddPurchase;

    @FXML
    private VBox cardsPane;

    @FXML
    private VBox recentPurchasesPriceCardPane;

    @FXML
    private ComboBox<TimeIntervalEnum> timeIntervalEnumComboBoxFilter;

    @FXML
    private ImageView imgType;

    @FXML
    private Label lblTotalPurchases;

    @FXML
    private Label lblTotalSpent;

    @FXML
    private Label lblTotalInLiters;

    // @FXML
    // private ComboBox<RawMaterial> rawMaterialComboBoxFilter;

    @FXML
    private ComboBox<SupplierView> supplierComboBoxFilter;

    @FXML
    private TextField txtSearch;

    @FXML
    public void initialize() {

        loadPurchases();
        configureTimeIntervalEnumComboBoxFilter();
        // configureRawMateialComboBoxFilter();
        configureSupplierComboBoxFilter();

        imgType.setImage(new Image(
                getClass().getResourceAsStream("/images/game-icons--basket.png")));
        imgType.getStyleClass().add("blue-icon");

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            filterCards(newValue);
        });

    }

    private void filterCards(String search) {

        if (search == null || search.isBlank()) {
            renderCards(filteredPurchases);
            renderRecentPurchasesPriceCard(filteredPurchases);
            return;
        }

        String query = search.toLowerCase();

        List<PurchaseView> filtered = filteredPurchases.stream()
                .filter(pView -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                            "dd 'de' MMMM 'de' yyyy",
                            Locale.forLanguageTag("pt-BR"));
                    return safe(pView.getPartnerName()).contains(safe(query)) ||
                            safe(pView.getDate().format(formatter)).contains(safe(query)) ||
                            safe(UtilsService.formatPrice(pView.getExpense())).contains(safe(query));
                })
                .toList();

        renderCards(filtered);
        renderRecentPurchasesPriceCard(filtered);
    }

    @FXML
    private void onAddPurchase() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/purchase-form.fxml"));

            loader.setControllerFactory(context::getBean);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.3;
            double height = screenBounds.getHeight() * 0.6;

            Stage dialog = new Stage();
            dialog.setTitle("Cadastrar Compra");
            dialog.setScene(new Scene(loader.load(), width, height));

            PurchaseFormController controller = loader.getController();
            // CALLBACK
            controller.setOnDataChanged(this::loadPurchases);

            Stage mainStage = (Stage) btnAddPurchase.getScene().getWindow();

            dialog.initOwner(mainStage);
            dialog.initModality(Modality.WINDOW_MODAL);

            dialog.setResizable(false);
            // stage.showAndWait();

            ColorAdjust darken = new ColorAdjust();
            darken.setBrightness(-0.8);
            mainStage.getScene().getRoot().setEffect(darken);

            dialog.setOnHidden(e -> mainStage.getScene().getRoot().setEffect(null));

            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void configureTimeIntervalEnumComboBoxFilter() {
        this.timeIntervalEnumComboBoxFilter.getItems().setAll(TimeIntervalEnum.values());

        this.timeIntervalEnumComboBoxFilter.setConverter(new StringConverter<>() {
            @Override
            public String toString(TimeIntervalEnum interval) {
                return interval == null ? "" : interval.getLabel();
            }

            @Override
            public TimeIntervalEnum fromString(String string) {
                return null;
            }
        });

        this.timeIntervalEnumComboBoxFilter.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null)
                return;
            applyFilters();
        });

        this.timeIntervalEnumComboBoxFilter.getSelectionModel().selectFirst();
    }

    /*
     * private void configureRawMateialComboBoxFilter() {
     * rawMaterialComboBoxFilter.getItems().setAll(
     * rawMaterialService.getRawMaterials());
     * 
     * rawMaterialComboBoxFilter.getItems().add(new RawMaterial());
     * rawMaterialComboBoxFilter.setConverter(new StringConverter<RawMaterial>() {
     * 
     * @Override
     * public String toString(RawMaterial rm) {
     * return (rm == null || rm.getName() == null) ? "Todos" : rm.getName();
     * }
     * 
     * @Override
     * public RawMaterial fromString(String s) {
     * return null;
     * }
     * });
     * 
     * rawMaterialComboBoxFilter.valueProperty().addListener((obs, old, newVal) -> {
     * 
     * applyFilters();
     * });
     * rawMaterialComboBoxFilter.getSelectionModel().selectLast();
     * }
     */

    private void configureSupplierComboBoxFilter() {
        supplierComboBoxFilter.getItems().setAll(
                supplierService.getSuppliers());

        supplierComboBoxFilter.getItems().add(new SupplierView());
        supplierComboBoxFilter.setConverter(new StringConverter<SupplierView>() {
            @Override
            public String toString(SupplierView s) {
                return (s == null || s.getName() == null) ? "Todos" : s.getName();
            }

            @Override
            public SupplierView fromString(String s) {
                return null;
            }
        });

        supplierComboBoxFilter.getSelectionModel().selectLast();

        supplierComboBoxFilter.valueProperty().addListener((obs, old, newVal) -> {

            applyFilters();
        });
    }

    private void applyFilters() {

        filteredPurchases = allPurchasesView.stream()

                // 🔹 filtro por matéria-prima
                /*
                 * .filter(p -> rawMaterialComboBoxFilter.getValue() == null
                 * || rawMaterialComboBoxFilter.getValue().getName() == null ||
                 * p.getRawMaterialName().equals(rawMaterialComboBoxFilter.getValue().getName())
                 * )
                 */

                // 🔹 filtro por fornecedor
                .filter(p -> supplierComboBoxFilter.getValue() == null
                        || supplierComboBoxFilter.getValue().getName() == null ||
                        p.getPartnerName().equals(supplierComboBoxFilter.getValue().getName()))

                // 🔹 filtro por intervalo de datas
                .filter(p -> {
                    if (timeIntervalEnumComboBoxFilter.getValue() == TimeIntervalEnum.ALL_TIME) {
                        return true;
                    }

                    LocalDate from = DateService.timeIntervalEnumToDateFrom(timeIntervalEnumComboBoxFilter.getValue());
                    LocalDate to = DateService.timeIntervalEnumToDateTo(timeIntervalEnumComboBoxFilter.getValue());

                    return !p.getDate().isBefore(from) && !p.getDate().isAfter(to);
                })

                .toList();

        renderCards(filteredPurchases);

        renderRecentPurchasesPriceCard(filteredPurchases);
    }

    private void renderCards(List<PurchaseView> purchasesView) {

        cardsPane.getChildren().clear();

        for (PurchaseView purchaseView : purchasesView) {
            cardsPane.getChildren().add(new PurchaseCard(purchaseView, this::loadPurchases, purchaseService,context));
        }
    }

    /*
     * private void renderCards(List<PurchaseView> purchasesView) {
     * 
     * cardsPane.getChildren().clear();
     * 
     * for (PurchaseView purchaseView : purchasesView) {
     * try {
     * FXMLLoader loader = new FXMLLoader(
     * getClass().getResource("/fxml/card/purchase-card.fxml"));
     * 
     * loader.setControllerFactory(context::getBean);
     * 
     * Parent card = loader.load();
     * 
     * PurchaseCardController controller = loader.getController();
     * controller.setPurchaseView(purchaseView);
     * controller.setOnDataChanged(this::reloadPurchases);
     * 
     * cardsPane.getChildren().add(card);
     * 
     * } catch (IOException e) {
     * e.printStackTrace();
     * }
     * }
     * }
     */

    private void renderRecentPurchasesPriceCard(List<PurchaseView> purchases) {
        lblTotalSpent.setText(UtilsService.formatPrice(purchaseService.calculateTotalSpent(purchases)));
        lblTotalPurchases.setText("" + purchases.size());
        lblTotalInLiters.setText(
                UtilsService.formatQuantity(purchaseService.calculateTotalTotalInLiters(purchases))
                        + " Litros (L) ");
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private void loadPurchases() {
        allPurchasesView = purchaseService.getPurchasesViewDateOrdenedReverse();

        filteredPurchases = allPurchasesView;

        renderCards(filteredPurchases);
        renderRecentPurchasesPriceCard(filteredPurchases);
    }

}
