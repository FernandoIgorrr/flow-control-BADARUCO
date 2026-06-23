package br.com.midnightsyslabs.flow_control.ui.controller.card;

import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.Scope;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.midnightsyslabs.flow_control.view.SupplierView;
import br.com.midnightsyslabs.flow_control.ui.utils.MaskUtils;
import br.com.midnightsyslabs.flow_control.view.PartnerCategory;
import br.com.midnightsyslabs.flow_control.service.SupplierService;
import br.com.midnightsyslabs.flow_control.repository.CityRepository;
import br.com.midnightsyslabs.flow_control.exception.ClientNotFoundException;
import br.com.midnightsyslabs.flow_control.repository.partner.CompanyPartnerRepository;
import br.com.midnightsyslabs.flow_control.repository.partner.PersonalPartnerRepository;
import br.com.midnightsyslabs.flow_control.ui.controller.form.PurchaseFormController;
import br.com.midnightsyslabs.flow_control.ui.controller.form.PurchaseSupplierFormController;
import br.com.midnightsyslabs.flow_control.ui.controller.form.edit.SupplierEditFormController;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Modality;

import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.text.TextFlow;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.effect.ColorAdjust;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.geometry.Rectangle2D;

@Controller
@Scope("prototype")
public class SupplierCardController {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private PersonalPartnerRepository personalPartnerRepository;

    @Autowired
    private CompanyPartnerRepository companyPartnerRepository;

    @Autowired
    private SupplierService supplierService;

    private SupplierView supplierView;

    private Runnable onDataChanged; // callback

    @FXML
    private Label lblName;
    @FXML
    private Label lblSubtitle;
    @FXML
    private Label lblPhone;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblDocument;
    @FXML
    private Label lblCity;
    @FXML
    private ImageView imgType;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAddPurchase;
    @FXML
    private ImageView btnIconDelete;
    @FXML
    private StackPane iconContainer;

    public void setSupplierView(SupplierView supplierView) {
        this.supplierView = supplierView;

        if (supplierView.isConfirmed()) {
            btnDelete.setDisable(true);
            btnIconDelete.getStyleClass().add("icon-delete-disable");

        } else {
            btnIconDelete.getStyleClass().add("icon-delete");
        }

        lblName.setText(supplierView.getName());

        String document = supplierView.getCategory() == PartnerCategory.PERSONAL
                ? "CPF: " + MaskUtils.applyMask(supplierView.getDocument(), "###.###.###-##")
                : "CNPJ: " + MaskUtils.applyMask(supplierView.getDocument(), "##.###.###/####-##");

        lblDocument.setText(document);
        lblPhone.setText("Tel: " + MaskUtils.applyMask(supplierView.getPhone(), "(##) #####-####"));
        lblEmail.setText("Email: " + supplierView.getEmail());
        lblCity.setText("Cidade: " + supplierView.getCity());

        if (supplierView.getCategory() == PartnerCategory.COMPANY) {
            lblSubtitle.setText("Companhia");
            lblSubtitle.getStyleClass().add("client-category-company");
            imgType.setImage(new Image(
                    getClass().getResourceAsStream("/images/company.png")));
            iconContainer.getStyleClass().add("icon-company");

        } else {
            lblSubtitle.setText("Pessoa Física");

            lblSubtitle.getStyleClass().add("client-category-personal");
            imgType.setImage(new Image(
                    getClass().getResourceAsStream("/images/person.png")));
            iconContainer.getStyleClass().add("icon-person");
        }
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    @FXML
    private void onEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/client-edit-form.fxml"));

            var controller = new SupplierEditFormController(
                    context.getBean(CompanyPartnerRepository.class),
                    context.getBean(PersonalPartnerRepository.class),
                    context.getBean(SupplierService.class),
                    context.getBean(CityRepository.class));

            controller.editSupplierForm(supplierView);
            loader.setControllerFactory(ctr -> controller);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.5;
            double height = screenBounds.getHeight() * 0.5;

            Stage dialog = new Stage();

            dialog.setTitle("Editar Cliente");

            dialog.setScene(new Scene(loader.load(), width, height));

            Stage mainStage = (Stage) btnEdit.getScene().getWindow();

            dialog.initOwner(mainStage);
            dialog.initModality(Modality.WINDOW_MODAL);

            dialog.setResizable(false);
            // stage.showAndWait();

            ColorAdjust darken = new ColorAdjust();
            darken.setBrightness(-0.8);
            mainStage.getScene().getRoot().setEffect(darken);

            dialog.setOnHidden(e -> mainStage.getScene().getRoot().setEffect(null));

            dialog.showAndWait();

            // avisa o controller pai
            if (onDataChanged != null) {
                onDataChanged.run();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("⚠️ CONFIRMAÇÃO DE EXCLUSÃO");
        alert.setHeaderText("VOCÊ TEM CERTEZA?");

        Label content = new Label(
                "Esta ação é IRREVERSÍVEL.\n\n" +
                        "O fornecedor " + supplierView.getName() + " será removido permanentemente do sistema.");
        content.setWrapText(true);

        Text warningText = new Text("Esta ação é IRREVERSÍVEL. ");
        warningText.getStyleClass().add("danger-text");

        Text startText = new Text("\n\nO fornecedor: ");
        startText.getStyleClass().add("common-text");

        Text clientName = new Text(supplierView.getName());
        clientName.getStyleClass().add("danger-name");

        Text endText = new Text(" será removido permanentemente do sistema.");
        endText.getStyleClass().add("common-text");

        TextFlow textFlow = new TextFlow(warningText, startText, clientName, endText);
        textFlow.setMaxWidth(420);

        alert.getDialogPane().setContent(textFlow);

        // Botões personalizados
        ButtonType cancelButton = new ButtonType("CANCELAR", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType deleteButton = new ButtonType("DELETAR", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(cancelButton, deleteButton);

        // Estilização após o dialog ser criado
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/css/alert-danger.css").toExternalForm());
        dialogPane.getStyleClass().add("danger-alert");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == deleteButton) {

            if (supplierView.getCategory() == PartnerCategory.PERSONAL) {
                personalPartnerRepository.findById(supplierView.getId()).ifPresentOrElse(supplier -> {
                    supplierService.deletePersonalSupplier(supplier);
                }, ClientNotFoundException::new);
            } else {
                companyPartnerRepository.findById(supplierView.getId()).ifPresentOrElse(supplier -> {
                    supplierService.deleteCompanySupplier(supplier);
                }, ClientNotFoundException::new);
            }
        }

        // avisa o controller pai
        if (onDataChanged != null) {
            onDataChanged.run();
        }
    }

    @FXML
    public void onAddPurchase() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/purchase-supplier-form.fxml"));

            loader.setControllerFactory(context::getBean);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.3;
            double height = screenBounds.getHeight() * 0.6;

            Stage dialog = new Stage();
            dialog.setTitle("Cadastrar Compra");
            dialog.setScene(new Scene(loader.load(), width, height));

            Stage mainStage = (Stage) btnAddPurchase.getScene().getWindow();

            dialog.initOwner(mainStage);
            dialog.initModality(Modality.WINDOW_MODAL);

            dialog.setResizable(false);

            PurchaseSupplierFormController controller = loader.getController();
            // CALLBACK
            controller.setSupplier(this.supplierView);

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
