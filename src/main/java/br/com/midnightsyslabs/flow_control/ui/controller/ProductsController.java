package br.com.midnightsyslabs.flow_control.ui.controller;

import java.util.List;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.midnightsyslabs.flow_control.repository.view.ProductFullRepository;
import br.com.midnightsyslabs.flow_control.service.ProductService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import br.com.midnightsyslabs.flow_control.ui.controller.card.ProductCardController;
import br.com.midnightsyslabs.flow_control.ui.controller.form.ProductFormController;
import br.com.midnightsyslabs.flow_control.view.ProductView;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.TilePane;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;

@Controller
public class ProductsController {

    @Autowired
    private ProductFullRepository productFullRepository;

    @Autowired
    private ApplicationContext context;

    private List<ProductView> products;

    @FXML
    private Button btnAddProduct;

    @FXML
    private TextField txtSearch;

    @FXML
    private TilePane cardsPane;

    @FXML
    public void initialize() {

        products = productFullRepository.findAll();

        renderCards(products);

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            filterCards(newValue);
        });
    }



    private void filterCards(String search) {

        if (search == null || search.isBlank()) {
            renderCards(products);
            return;
        }

        String query = search.toLowerCase();

        List<ProductView> filtered = products.stream()
                .filter(p -> safe(p.getName()).contains(safe(query)) ||
                        safe(p.getDescription()).contains(safe(query)) ||
                        safe(UtilsService.formatPrice(p.getCurrentPrice())).contains(safe(query)) ||
                        safe(UtilsService.formatQuantity(p.getQuantity())).contains(safe(query)) ||
                        safe(p.getMeasurementUnitName()).contains(safe(query)))
                .toList();

        renderCards(filtered);
    }

    private void renderCards(List<ProductView> products) {

        cardsPane.getChildren().clear();

        for (ProductView product : products) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/card/product-card.fxml"));

                loader.setControllerFactory(context::getBean);

                Parent card = loader.load();

                ProductCardController controller = loader.getController();
                controller.setProductView(product);
                controller.setOnDataChanged(this::reloadProducts);
                
                cardsPane.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    
    private void reloadProducts() {
        this.products = productFullRepository.findAll();
        filterCards(txtSearch.getText());
    }

    @FXML
    private void onAddProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/product-form.fxml"));

            loader.setControllerFactory(context::getBean);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.3;
            double height = screenBounds.getHeight() * 0.5;

            Stage dialog = new Stage();
            dialog.setTitle("Cadastrar Produto");
            dialog.setScene(new Scene(loader.load(), width, height));

            ProductFormController controller = loader.getController();
            //  CALLBACK
            controller.setOnDataChanged(this::reloadProducts);

            Stage mainStage = (Stage) btnAddProduct.getScene().getWindow();

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
}
