package br.com.midnightsyslabs.flow_control.ui.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.service.ProductionService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import br.com.midnightsyslabs.flow_control.ui.cards.ProductionCard;
import br.com.midnightsyslabs.flow_control.ui.controller.form.ProductionFormController;
import br.com.midnightsyslabs.flow_control.view.ProductionView;
import br.com.midnightsyslabs.flow_control.view.PurchaseView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

@Controller
public class ProductionsController {

    @Autowired
    private ProductionService productionService;

    @Autowired
    private ApplicationContext context;

    private List<ProductionView> productionsView;

    @FXML
    private Button btnAddProduction;

    @FXML
    private TextField txtSearch;

    @FXML
    private TilePane cardsPane;

    @FXML
    public void initialize(){
        loadProductions();

         txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            filterCards(newValue);
        });
    }

     private void filterCards(String search) {

        if (search == null || search.isBlank()) {
            renderCards(productionsView);
            return;
        }

        String query = search.toLowerCase();

        List<ProductionView> filtered = productionsView.stream()
                .filter(pView -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                            "dd 'de' MMMM 'de' yyyy",
                            Locale.forLanguageTag("pt-BR"));
                    return  safe(pView.getDate().format(formatter)).contains(safe(query));
                })
                .toList();

        renderCards(filtered);
    }

    @FXML
    private void onAddProduction() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/production-form.fxml"));

            loader.setControllerFactory(context::getBean);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.3;
            double height = screenBounds.getHeight() * 0.7;

            Stage dialog = new Stage();
            dialog.setTitle("Cadastrar Produção");
            dialog.setScene(new Scene(loader.load(), width, height));

             ProductionFormController controller = loader.getController();
            // CALLBACK
             controller.setOnDataChanged(this::loadProductions);

            Stage mainStage = (Stage) btnAddProduction.getScene().getWindow();

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

    private void renderCards(List<ProductionView> productions) {

        cardsPane.getChildren().clear();

        for (var pView : productions) {
            cardsPane.getChildren().add(new ProductionCard(pView,productionService,this::loadProductions,context));
        }
    }

    public void loadProductions() {
        productionsView = productionService.getProductionsView();
        renderCards(productionsView);
       
    }

    public String safe(String str){
        return str.toLowerCase();
    }
}
