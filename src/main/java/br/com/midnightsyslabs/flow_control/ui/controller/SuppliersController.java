package br.com.midnightsyslabs.flow_control.ui.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.service.SupplierService;
import br.com.midnightsyslabs.flow_control.ui.controller.card.SupplierCardController;
import br.com.midnightsyslabs.flow_control.ui.controller.form.SupplierFormController;
import br.com.midnightsyslabs.flow_control.view.SupplierView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

@Controller
public class SuppliersController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ApplicationContext context;

    private List<SupplierView> suppliers;

    @FXML
    private Button btnAddSupplier;

    @FXML
    private TextField txtSearch;

    @FXML
    private TilePane cardsPane;

    @FXML
    private Label lblNumberOfSuppliers;

    @FXML
    public void initialize() {

        loadSuppliers();

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            filterCards(newValue);
        });
    }

    private void filterCards(String search) {

        if (search == null || search.isBlank()) {
            renderCards(suppliers);
            return;
        }

        String query = search.toLowerCase();

        List<SupplierView> filtered = suppliers.stream()
                .filter(s -> safe(s.getName()).contains(query) ||
                        safe(s.getCity()).contains(query) ||
                        safe(s.getDocument()).contains(query))
                .toList();

        renderCards(filtered);
    }

    private void renderCards(List<SupplierView> suppliers) {

        cardsPane.getChildren().clear();

        for (SupplierView supplier : suppliers) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/card/supplier-card.fxml"));

                loader.setControllerFactory(context::getBean);

                Parent card = loader.load();

                SupplierCardController controller = loader.getController();
                controller.setSupplierView(supplier);

                // CALLBACK
                controller.setOnDataChanged(this::loadSuppliers);

                cardsPane.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private void loadSuppliers() {
        this.suppliers = supplierService.getSuppliers();
        lblNumberOfSuppliers.setText("Número de fornecedores: " + suppliers.size());
        filterCards(txtSearch.getText());
    }

    @FXML
    private void onAddSupplier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/supplier-form.fxml"));

            loader.setControllerFactory(context::getBean);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.5;
            double height = screenBounds.getHeight() * 0.5;

            Stage dialog = new Stage();
            dialog.setTitle("Cadastrar Fornecedor");
            dialog.setScene(new Scene(loader.load(), width, height));

            SupplierFormController controller = loader.getController();
            // CALLBACK
            controller.setOnDataChanged(this::loadSuppliers);

            Stage mainStage = (Stage) btnAddSupplier.getScene().getWindow();

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
