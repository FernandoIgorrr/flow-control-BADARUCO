package br.com.midnightsyslabs.flow_control.ui.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.config.TimeIntervalEnum;
import br.com.midnightsyslabs.flow_control.domain.entity.product.Product;
import br.com.midnightsyslabs.flow_control.dto.SaleDTO;
import br.com.midnightsyslabs.flow_control.service.ClientService;
import br.com.midnightsyslabs.flow_control.service.DateService;
import br.com.midnightsyslabs.flow_control.service.ProductService;
import br.com.midnightsyslabs.flow_control.service.SaleService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import br.com.midnightsyslabs.flow_control.ui.cards.SaleCard;
import br.com.midnightsyslabs.flow_control.ui.controller.form.SaleFormController;
import br.com.midnightsyslabs.flow_control.view.ClientView;
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
public class SalesController {

    @Autowired
    private SaleService saleService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ApplicationContext context;

    private List<SaleDTO> allSalesDTO;

    private List<SaleDTO> filteredSalesDTO;

    @FXML
    private ComboBox<TimeIntervalEnum> timeIntervalEnumComboBoxFilter;

    @FXML
    private ImageView imgType;

    @FXML
    private Label lblTotalSales;

    @FXML
    private Label lblTotalPrice;

    @FXML
    private ComboBox<Product> productComboBoxFilter;

    @FXML
    private ComboBox<ClientView> clientComboBoxFilter;

    @FXML
    private Button btnAddSale;

    @FXML
    private TextField txtSearch;

    @FXML
    private VBox cardsPane;

    @FXML
    public void initialize() {

        loadSales();

        configureTimeIntervalEnumComboBoxFilter();
        configureProductComboBoxFilter();
        configureClientComboBoxFilter();

        imgType.setImage(new Image(
                getClass().getResourceAsStream("/images/game-icons--basket.png")));
        imgType.getStyleClass().add("blue-icon");

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            filterCards(newValue);
        });
    }

    private void filterCards(String search) {

        if (search == null || search.isBlank()) {
            renderCards(filteredSalesDTO);
            renderRecentSilesPriceCard(filteredSalesDTO);
            return;
        }

        String query = search.toLowerCase();

        List<SaleDTO> filtered = filteredSalesDTO.stream()
                .filter(sDTO -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                            "dd 'de' MMMM 'de' yyyy",
                            Locale.forLanguageTag("pt-BR"));
                    return safe(sDTO.getClientCategory()).contains(safe(query)) ||
                            safe(sDTO.getClientName()).contains(safe(query)) ||
                            safe(sDTO.getDate().format(formatter)).contains(safe(query)) ||
                            safe(UtilsService.formatPrice(sDTO.getRevenue())).contains(safe(query));
                })
                .toList();

        renderCards(filtered);
        renderRecentSilesPriceCard(filtered);
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

    private void configureProductComboBoxFilter() {
        productComboBoxFilter.getItems().setAll(
                productService.getProducts());

        productComboBoxFilter.getItems().add(new Product());
        productComboBoxFilter.setConverter(new StringConverter<Product>() {
            @Override
            public String toString(Product p) {
                return (p == null || p.getName() == null) ? "Todos" : p.getName() + " | " + p.getDescription();
            }

            @Override
            public Product fromString(String s) {
                return null;
            }
        });

        productComboBoxFilter.valueProperty().addListener((obs, old, newVal) -> {

            applyFilters();
        });
        productComboBoxFilter.getSelectionModel().selectLast();
    }

    private void configureClientComboBoxFilter() {
        clientComboBoxFilter.getItems().setAll(
                clientService.getClients());

        clientComboBoxFilter.getItems().add(new ClientView());
        clientComboBoxFilter.setConverter(new StringConverter<ClientView>() {
            @Override
            public String toString(ClientView c) {
                return (c == null || c.getName() == null) ? "Todos" : c.getName();
            }

            @Override
            public ClientView fromString(String s) {
                return null;
            }
        });

        clientComboBoxFilter.getSelectionModel().selectLast();

        clientComboBoxFilter.valueProperty().addListener((obs, old, newVal) -> {

            applyFilters();
        });
    }

    @FXML
    private void onAddSale() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/sale-form.fxml"));

            loader.setControllerFactory(context::getBean);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.3;
            double height = screenBounds.getHeight() * 0.7;

            Stage dialog = new Stage();
            dialog.setTitle("Cadastrar Venda");
            dialog.setScene(new Scene(loader.load(), width, height));

            SaleFormController controller = loader.getController();
            controller.setOnDataChanged(this::loadSales);

            Stage mainStage = (Stage) btnAddSale.getScene().getWindow();

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

    private void renderCards(List<SaleDTO> salesDTO) {

        cardsPane.getChildren().clear();

        for (var sDTO : salesDTO) {
            cardsPane.getChildren().add(new SaleCard(sDTO, this::loadSales, saleService));
        }
    }

    private void applyFilters() {

        filteredSalesDTO = allSalesDTO.stream()

                // 🔹 filtro por produto vendido
                .filter(sDTO -> productComboBoxFilter.getValue() == null
                        || productComboBoxFilter.getValue().getName() == null ||
                        sDTO.getSaleProductsView().stream().anyMatch(spv -> spv.getProductId() != null
                                && spv.getProductId() != null
                                && spv.getProductId()
                                        .equals(productComboBoxFilter.getValue().getId())))

                // 🔹 filtro por cliente
                .filter(sDTO -> clientComboBoxFilter.getValue() == null
                        || clientComboBoxFilter.getValue().getName() == null ||
                        sDTO.getClientId().equals(clientComboBoxFilter.getValue().getId()))

                // 🔹 filtro por intervalo de datas
                .filter(sDTO -> {
                    if (timeIntervalEnumComboBoxFilter.getValue() == TimeIntervalEnum.ALL_TIME) {
                        return true;
                    }

                    LocalDate from = DateService.timeIntervalEnumToDateFrom(timeIntervalEnumComboBoxFilter.getValue());
                    LocalDate to = DateService.timeIntervalEnumToDateTo(timeIntervalEnumComboBoxFilter.getValue());

                    return !sDTO.getDate().isBefore(from) && !sDTO.getDate().isAfter(to);
                })

                .toList();

        renderCards(filteredSalesDTO);

        renderRecentSilesPriceCard(filteredSalesDTO);
    }

    private void renderRecentSilesPriceCard(List<SaleDTO> salesDTO) {
        lblTotalPrice.setText(UtilsService.formatPrice(saleService.calculateTotalRevenue(salesDTO)));
        lblTotalSales.setText("" + salesDTO.size());
    }

    public void loadSales() {
        allSalesDTO = saleService.getSalesDTO();
        filteredSalesDTO = allSalesDTO;
        renderCards(filteredSalesDTO);
        renderRecentSilesPriceCard(filteredSalesDTO);
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}
