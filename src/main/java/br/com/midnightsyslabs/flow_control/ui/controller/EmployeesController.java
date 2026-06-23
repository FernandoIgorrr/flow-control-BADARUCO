package br.com.midnightsyslabs.flow_control.ui.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.domain.entity.employee.Employee;
import br.com.midnightsyslabs.flow_control.domain.entity.raw_material.RawMaterial;
import br.com.midnightsyslabs.flow_control.service.EmployeeService;
import br.com.midnightsyslabs.flow_control.ui.controller.form.ClientFormController;
import br.com.midnightsyslabs.flow_control.ui.controller.form.EmployeeFormController;
import br.com.midnightsyslabs.flow_control.view.ClientView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
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
public class EmployeesController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ApplicationContext context;

    List<Employee> allEmployees;

    List<Employee> filteredEmployees;

    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    private TableColumn<Employee, String> colName;
    @FXML
    private TableColumn<Employee, Void> colEdit;

    @FXML
    private TableColumn<Employee, Void> colDisconnect;

    @FXML
    private Button btnAddEmployee;

    @FXML
    private Button btnAddEmployeePayments;

    @FXML
    private TextField txtSearch;

    @FXML
    public void initialize() {

        employeeTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        loadEmployees();

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

        filteredEmployees = allEmployees.stream()
                .filter(e -> safe(e.getName()).contains(query))
                .toList();
        reloadEmployees();
    }

    @FXML
    public void onAddEmployee() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/employee-form.fxml"));

            loader.setControllerFactory(context::getBean);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.3;
            double height = screenBounds.getHeight() * 0.3;

            Stage dialog = new Stage();
            dialog.setTitle("Cadastrar Funcionário");
            dialog.setScene(new Scene(loader.load(), width, height));

            EmployeeFormController controller = loader.getController();
            // CALLBACK
            controller.setOnDataChanged(this::loadEmployees);

            Stage mainStage = (Stage) btnAddEmployee.getScene().getWindow();

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
                    Employee employee = getTableView()
                            .getItems()
                            .get(getIndex());

                    disconnectEmployee(employee);
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

                Employee employee = getTableView()
                        .getItems()
                        .get(getIndex());

                if (!(employee.getDeletedAt() == null)) {
                    btnConnect.setOnAction(e -> connectEmployee(employee));
                    setGraphic(btnConnect);
                } else {
                    btnDisconnect.setOnAction(e -> disconnectEmployee(employee));
                    setGraphic(btnDisconnect);
                }
            }
        });
    }

    @FXML
    public void onAddEmployeePayments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/employee-payments-form.fxml"));

            loader.setControllerFactory(context::getBean);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.3;
            double height = screenBounds.getHeight() * 0.5;

            Stage dialog = new Stage();
            dialog.setTitle("Cadastrar Pagamentos");
            dialog.setScene(new Scene(loader.load(), width, height));

            Stage mainStage = (Stage) btnAddEmployee.getScene().getWindow();

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

    public void disconnectEmployee(Employee employee) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Desconectar funcionário");
        alert.setContentText(
                "Tem certeza que deseja desconectar " + employee.getName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                employeeService.disconnectEmployee(employee);
                loadEmployees();
            }
        });
    }

    public void connectEmployee(Employee employee) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Reconectar funcionário");
        alert.setContentText(
                "Tem certeza que deseja reconectar " + employee.getName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                employeeService.connectEmployee(employee);
                loadEmployees();
            }
        });
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    public void loadEmployees() {
        allEmployees = employeeService.getAllEmployees();

        filteredEmployees = allEmployees;

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        colName.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);

                if (empty || name == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                Employee employee = getTableView()
                        .getItems()
                        .get(getIndex());

                setText(name);

                if (!(employee.getDeletedAt() == null)) {
                    setStyle("-fx-text-fill: red;-fx-font-size: 18;-fx-alignment: CENTER;");
                } else {

                }
            }
        });

        addDisconnectButton();

        employeeTable.getItems().setAll(filteredEmployees);
        // ATUALIZA O HEADER DA COLUNA
        colName.setText(
                "Funcionários (" + allEmployees.stream().filter(e -> e.getDeletedAt() == null).toList().size() + ")");
    }

    public void reloadEmployees() {
        employeeTable.getItems().setAll(filteredEmployees);
    }
}
