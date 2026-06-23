package br.com.midnightsyslabs.flow_control.ui.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.midnightsyslabs.flow_control.service.ClientService;
import br.com.midnightsyslabs.flow_control.ui.cards.ClientCard;
import br.com.midnightsyslabs.flow_control.ui.controller.form.ClientFormController;
import br.com.midnightsyslabs.flow_control.view.ClientView;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.TilePane;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;

@Controller
public class ClientsController {
    @Autowired
    private ClientService clientService;

    // @Autowired
    // private ClientRepository clientRepository;

    @Autowired
    private ApplicationContext context;

    private List<ClientView> clients;

    @FXML
    private Button btnAddClient;

    @FXML
    private TextField txtSearch;

    @FXML
    private TilePane cardsPane;

    @FXML
    private Label lblNumberOfClients;

    @FXML
    public void initialize() {

        loadClients();

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            filterCards(newValue);
        });
    }

    private void filterCards(String search) {

        if (search == null || search.isBlank()) {
            renderCards(clients);
            return;
        }

        String query = search.toLowerCase();

        List<ClientView> filtered = clients.stream()
                .filter(c -> safe(c.getName()).contains(safe(query)) ||
                        safe(c.getCity()).contains(safe(query)) ||
                        safe(c.getDocument()).contains(safe(query)))
                .toList();

        renderCards(filtered);
    }

    private void renderCards(List<ClientView> clientsView) {

        cardsPane.getChildren().clear();

        for (ClientView cView : clientsView) {
            cardsPane.getChildren().add(new ClientCard(cView, this::loadClients, clientService, context));
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    /* private void reloadClients() {
        clients = clientService.getClients();
        filterCards(txtSearch.getText());
    } */

    @FXML
    private void onAddClient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/client-form.fxml"));

            loader.setControllerFactory(context::getBean);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.5;
            double height = screenBounds.getHeight() * 0.5;

            Stage dialog = new Stage();
            dialog.setTitle("Cadastrar Cliente");
            dialog.setScene(new Scene(loader.load(), width, height));

            ClientFormController controller = loader.getController();
            // CALLBACK
            controller.setOnDataChanged(this::loadClients);

            Stage mainStage = (Stage) btnAddClient.getScene().getWindow();

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

    private void loadClients() {
        clients = clientService.getClients();
        lblNumberOfClients.setText("Número de clientes: " + clients.size());
        renderCards(clients);
    }
}
