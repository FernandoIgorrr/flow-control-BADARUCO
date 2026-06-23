package br.com.midnightsyslabs.flow_control_badaruco.ui.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.midnightsyslabs.flow_control_badaruco.service.DatabaseBackupService;
import br.com.midnightsyslabs.flow_control_badaruco.service.NavigationService;
import br.com.midnightsyslabs.flow_control_badaruco.ui.utils.UiUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

@Component
public class MainController {

    @Autowired
    private DatabaseBackupService databaseBackupService;

    @Autowired
    private NavigationService navService;

    @FXML
    private StackPane conteudoPrincipal;

    @FXML
    private VBox sidebar;

    @FXML
    private VBox sidebarButtons;

    @FXML
    private Button statementFinanceButton;

    /*
     * @FXML
     * private Button dashboardButton;
     */

    @FXML
    private Button productsButton;

    /*
     * @FXML
     * private Button rawMaterialsButton;
     */

    @FXML
    private Button clientsButton;

    @FXML
    private Button suppliersButton;

    @FXML
    private Button productionsButton;

    @FXML
    private Button salesButton;

    @FXML
    private Button purchasesButton;

    @FXML
    private Button expensesButton;

    @FXML
    private Button employeesButton;

    @FXML 
    private Button vehiclesButton;

    @FXML
    private ImageView logo;

    @FXML
    private Button btnBackup;

    @FXML
    public void initialize() {
        logo.setImage(new Image(
                getClass().getResourceAsStream("/images/logo.png")));
        navService.setMainContainer(conteudoPrincipal);
        // Carrega uma tela inicial opcional
        navService.navigateTo("/fxml/statement-finance.fxml");
        setActive(statementFinanceButton);

    }

    private void setActive(Button activeButton) {
        sidebarButtons.getChildren().forEach(node -> {
            if (node instanceof Button btn) {
                btn.getStyleClass().remove("selected");
            }
        });

        activeButton.getStyleClass().add("selected");
    }

    /*
     * @FXML
     * public void goToDashboard() {
     * navService.navigateTo("/fxml/dashboard.fxml");
     * setActive(dashboardButton);
     * }
     */

    @FXML
    public void goToProducts() {
        navService.navigateTo("/fxml/products.fxml");
        setActive(productsButton);
    }

    /*
     * @FXML
     * public void goToRawMaterials() {
     * navService.navigateTo("/fxml/raw-materials.fxml");
     * setActive(rawMaterialsButton);
     * }
     */

    @FXML
    public void goToClients() {
        navService.navigateTo("/fxml/clients.fxml");
        setActive(clientsButton);
    }

    @FXML
    public void goToSuppliers() {
        navService.navigateTo("/fxml/suppliers.fxml");
        setActive(suppliersButton);
    }

    @FXML
    public void goToProductions() {
        navService.navigateTo("/fxml/productions.fxml");
        setActive(productionsButton);
    }

    @FXML
    public void goToSales() {
        navService.navigateTo("/fxml/sales.fxml");
        setActive(salesButton);
    }

    @FXML
    public void goToPurchases() {
        navService.navigateTo("/fxml/purchases.fxml");
        setActive(purchasesButton);
    }

    @FXML
    public void goToExpenses() {
        navService.navigateTo("/fxml/expenses.fxml");
        setActive(expensesButton);
    }

    @FXML
    public void goToStatementFinance() {
        navService.navigateTo("/fxml/statement-finance.fxml");
        setActive(statementFinanceButton);
    }

    @FXML
    public void goToEmployees() {
        navService.navigateTo("/fxml/employees.fxml");
        setActive(employeesButton);
    }
    
    @FXML
    public void goToVehicles() {
        navService.navigateTo("/fxml/vehicles.fxml");
        setActive(vehiclesButton);
    }

    @FXML
    public void doBackup() {

        try {
            // 📁 pasta onde o app está rodando
            Path appDir = Paths.get(System.getProperty("user.dir"));
            Path backupDir = appDir.resolve("backups");

            // cria a pasta se não existir
            if (Files.notExists(backupDir)) {
                Files.createDirectories(backupDir);
            }

            // 📅 nome automático do arquivo
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

            String fileName = LocalDateTime.now().format(formatter)
                    + "_backup_flow_control_badaruco.sql";

            Path backupFile = backupDir.resolve(fileName);

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    databaseBackupService.backup(backupFile.toString());
                    return null;
                }
            };

            task.setOnSucceeded(e -> UiUtils.showLabelAlert(AlertType.INFORMATION, "Backup Successful",
                    "Backup concluído com sucesso!"));

            task.setOnFailed(e -> UiUtils.showLabelAlert(AlertType.ERROR, "Backup Error",
                    "Erro no backup: " + task.getException().getMessage()));

            new Thread(task).start();

        } catch (Exception e) {
            UiUtils.showLabelAlert(AlertType.ERROR, "Backup Error", "Erro ao preparar backup: " + e.getMessage());
        }
    }
}