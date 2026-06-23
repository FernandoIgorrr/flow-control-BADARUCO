package br.com.midnightsyslabs.flow_control.ui.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.domain.entity.raw_material.RawMaterial;
import br.com.midnightsyslabs.flow_control.repository.RawMaterialRepository;
import br.com.midnightsyslabs.flow_control.ui.controller.card.RawMaterialCardController;
import br.com.midnightsyslabs.flow_control.ui.controller.form.RawMaterialFormController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

@Controller
public class RawMaterialsController {
     @Autowired
    private RawMaterialRepository rawMaterialRepository;

    @Autowired
    private ApplicationContext context;

    private List<RawMaterial> rawMaterials;

    @FXML
    private Button btnAddRawMaterial;

    @FXML
    private TextField txtSearch;

    @FXML
    private TilePane cardsPane;

    @FXML
    public void initialize() {

        rawMaterials = rawMaterialRepository.findAllNonDeleted();

        renderCards(rawMaterials);

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            filterCards(newValue);
        });
    }



    private void filterCards(String search) {

        if (search == null || search.isBlank()) {
            renderCards(rawMaterials);
            return;
        }

        String query = search.toLowerCase();

        List<RawMaterial> filtered = rawMaterials.stream()
                .filter(p -> safe(p.getName()).contains(query) ||
                        safe(p.getDescription()).contains(query)
                      )
                .toList();

        renderCards(filtered);
    }

    private void renderCards(List<RawMaterial> rawMaterials) {

        cardsPane.getChildren().clear();

        for (RawMaterial rawMaterial : rawMaterials) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/card/raw-material-card.fxml"));

                loader.setControllerFactory(context::getBean);

                Parent card = loader.load();

                RawMaterialCardController controller = loader.getController();
                controller.setRawMaterial(rawMaterial);
                controller.setOnDataChanged(this::reloadRawMaterials);
                
                cardsPane.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    
    private void reloadRawMaterials() {
        this.rawMaterials = rawMaterialRepository.findAllNonDeleted();
        filterCards(txtSearch.getText());
    }

    @FXML
    private void onAddRawMaterial() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/raw-material-form.fxml"));

            loader.setControllerFactory(context::getBean);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.2;
            double height = screenBounds.getHeight() * 0.4;

            Stage dialog = new Stage();
            dialog.setTitle("Cadastrar Matéria-Prima");
            dialog.setScene(new Scene(loader.load(), width, height));

            RawMaterialFormController controller = loader.getController();
            //  CALLBACK
            controller.setOnDataChanged(this::reloadRawMaterials);

            Stage mainStage = (Stage) btnAddRawMaterial.getScene().getWindow();

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
