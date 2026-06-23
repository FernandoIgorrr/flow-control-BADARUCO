package br.com.midnightsyslabs.flow_control.ui.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.domain.entity.vehicle.Vehicle;
import br.com.midnightsyslabs.flow_control.service.VehicleService;
import br.com.midnightsyslabs.flow_control.ui.controller.form.VehicleFormController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

@Controller
public class VehiclesController {
    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private ApplicationContext context;

    List<Vehicle> allVehicles;

    List<Vehicle> filteredVehicles;

    @FXML
    private TableView<Vehicle> vehicleTable;

    @FXML
    private TableColumn<Vehicle, String> colNumberPlate;

    @FXML
    private TableColumn<Vehicle, String> colModel;

    @FXML
    private TableColumn<Vehicle, Void> colEdit;

    @FXML
    private TableColumn<Vehicle, Void> colDisconnect;

    @FXML
    private Button btnAddVehicle;

    @FXML
    private TextField txtSearch;

    @FXML
    public void initialize() {

        vehicleTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        loadVehicles();

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            filterList(newValue);
        });
    }

    private void filterList(String search) {

        /*
         * if (search == null || search.isBlank()) {
         * reloadEmployees();
         * return;
         * }
         */
        String query = search.toLowerCase();

        filteredVehicles = allVehicles.stream()
                .filter(v -> safe(v.getNumberPlate()).contains(query))
                .toList();
        reloadVehicles();
    }

    public void loadVehicles() {
        allVehicles = vehicleService.getVehicles();

        filteredVehicles = allVehicles;

        colNumberPlate.setCellValueFactory(new PropertyValueFactory<>("numberPlate"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));

        colNumberPlate.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(String number_plate, boolean empty) {
                super.updateItem(number_plate, empty);

                if (empty || number_plate == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                Vehicle vehicle = getTableView()
                        .getItems()
                        .get(getIndex());

                setText(number_plate);

                if (!(vehicle.getDeletedAt() == null)) {
                    setStyle("-fx-text-fill: red;-fx-font-size: 18;-fx-alignment: CENTER;");
                } else {

                }
            }
        });

        colModel.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(String model, boolean empty) {
                super.updateItem(model, empty);

                if (empty || model == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                Vehicle vehicle = getTableView()
                        .getItems()
                        .get(getIndex());

                setText(model);

                if (!(vehicle.getDeletedAt() == null)) {
                    setStyle("-fx-text-fill: red;-fx-font-size: 18;-fx-alignment: CENTER;");
                } else {

                }
            }
        });

        addDisconnectButton();

        vehicleTable.getItems().setAll(filteredVehicles);
        // ATUALIZA O HEADER DA COLUNA
        colNumberPlate.setText(
                "Placa (" + allVehicles.stream().filter(e -> e.getDeletedAt() == null).toList().size() + ")");
    }

     @FXML
    public void onAddVehicle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/vehicle-form.fxml"));

            loader.setControllerFactory(context::getBean);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.3;
            double height = screenBounds.getHeight() * 0.4;

            Stage dialog = new Stage();
            dialog.setTitle("Cadastrar Veículo");
            dialog.setScene(new Scene(loader.load(), width, height));

            VehicleFormController controller = loader.getController();
            // CALLBACK
            controller.setOnDataChanged(this::loadVehicles);

            Stage mainStage = (Stage) btnAddVehicle.getScene().getWindow();

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


    private void addDisconnectButton() {
        colDisconnect.setCellFactory(param -> new TableCell<>() {

            private final Button btnDisconnect = new Button();
            private final Button btnConnect = new Button();

            {
                // 🔴 BOTÃO DESCONECTAR

                ImageView iconDisconnect = new ImageView(
                        new Image(getClass().getResourceAsStream("/images/carbon--close-outline.png")));

                iconDisconnect.setFitWidth(32);
                iconDisconnect.setFitHeight(32);
                iconDisconnect.setPreserveRatio(true);
                iconDisconnect.getStyleClass().add("icon-delete");

                btnDisconnect.setGraphic(iconDisconnect);
                btnDisconnect.getStyleClass().add("btn-action-delete");
                btnDisconnect.setOnAction(event -> {
                    Vehicle vehicle = getTableView()
                            .getItems()
                            .get(getIndex());

                    disconnectVehicle(vehicle);
                });

                // 🟢 BOTÃO CONECTAR
                ImageView iconConnect = new ImageView(
                        new Image(getClass().getResourceAsStream(
                                "/images/line-md--confirm-circle.png")));

                iconConnect.setFitWidth(36);
                iconConnect.setFitHeight(36);
                iconConnect.setPreserveRatio(true);
                iconConnect.getStyleClass().add("icon-confirm");

                btnConnect.setGraphic(iconConnect);
                btnConnect.getStyleClass().add("btn-action-edit");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Vehicle vehicle = getTableView()
                        .getItems()
                        .get(getIndex());

                if (!(vehicle.getDeletedAt() == null)) {
                    btnConnect.setOnAction(e -> connectVehicle(vehicle));
                    setGraphic(btnConnect);
                } else {
                    btnDisconnect.setOnAction(e -> disconnectVehicle(vehicle));
                    setGraphic(btnDisconnect);
                }
            }
        });
    }

    public void disconnectVehicle(Vehicle vehicle) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Desconectar veículo");
        alert.setContentText(
                "Tem certeza que deseja desconectar " + vehicle.getNumberPlate() + " - " + vehicle.getModel() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                vehicleService.disconnectVehicle(vehicle);
                loadVehicles();
            }
        });
    }

    public void connectVehicle(Vehicle vehicle) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Reconectar veículo");
        alert.setContentText(
                "Tem certeza que deseja reconectar " + vehicle.getNumberPlate() + " - " + vehicle.getModel() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                vehicleService.connectVehicle(vehicle);
                loadVehicles();
            }
        });
    }

    public void reloadVehicles() {
        vehicleTable.getItems().setAll(filteredVehicles);
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}
