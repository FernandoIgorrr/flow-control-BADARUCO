package br.com.midnightsyslabs.flow_control.ui.controller.card;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.exception.ProductNotFoundException;
import br.com.midnightsyslabs.flow_control.repository.product.ProductRepository;
import br.com.midnightsyslabs.flow_control.service.ProductService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import br.com.midnightsyslabs.flow_control.ui.controller.form.edit.ProductEditFormController;
import br.com.midnightsyslabs.flow_control.view.ProductView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

@Controller
@Scope("prototype")
public class ProductCardController {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    private Runnable onDataChanged; // callback

    @FXML
    private Label lblName;
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblQuantity;
    @FXML
    private Label lblMeasurementUnitName;
    @FXML
    private Label lblMeasurementUnitSymbol;
    @FXML
    private Label lblMeasurementUnitUnit;
    @FXML
    private Label lblCurrentPrice;
    @FXML
    private ImageView imgType;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    @FXML
    private ImageView btnIconDelete;

    private ProductView productView;

    public void setProductView(ProductView productView) {
        this.productView = productView;

        if (productView.isConfirmed()) {
            btnDelete.setDisable(true);
            btnIconDelete.getStyleClass().add("icon-delete-disable");

        } else {
            btnIconDelete.getStyleClass().add("icon-delete");
        }

        lblName.setText(this.productView.getName());
        lblDescription.setText(this.productView.getDescription());
        lblQuantity.setText(UtilsService.formatQuantity(this.productView.getQuantity()));
        lblMeasurementUnitUnit.setText(this.productView.getMeasurementUnitUnit() + ": ");
        lblMeasurementUnitName.setText(this.productView.getMeasurementUnitName());
        lblMeasurementUnitSymbol.setText("(" + this.productView.getMeasurementUnitSymbol() + ")");
        lblCurrentPrice.setText("(R$) " + UtilsService.formatPrice(this.productView.getCurrentPrice()));

        if (safe(productView.getCategory()).equals("queijo")) {
            imgType.setImage(new Image(
                    getClass().getResourceAsStream("/images/queijo.png")));
        } else if (safe(productView.getCategory()).equals("nata")) {
            imgType.setImage(new Image(
                    getClass().getResourceAsStream("/images/nata.png")));
        } else if (safe(productView.getCategory()).equals("iogurte")) {
            imgType.setImage(new Image(
                    getClass().getResourceAsStream("/images/iogurte.png")));
        } else if (safe(productView.getCategory()).equals("manteiga")) {
            imgType.setImage(new Image(
                    getClass().getResourceAsStream("/images/twemoji--butter.png")));
        }

    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    public void onEdit() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/product-edit-form.fxml"));

            var controller = new ProductEditFormController(
                    context.getBean(ProductService.class),
                    context.getBean(ProductRepository.class));

            controller.editProductForm(this.productView);
            loader.setControllerFactory(ctr -> controller);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.3;
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

    public void onDelete() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("⚠️ CONFIRMAÇÃO DE EXCLUSÃO");
        alert.setHeaderText("VOCÊ TEM CERTEZA?");
        /*
         * alert.setContentText(
         * "Esta ação é IRREVERSÍVEL.\n\n" +
         * "O cliente " + clientDTO.getName() +
         * " será removido permanentemente do sistema.");
         */

        Label content = new Label(
                "Esta ação é IRREVERSÍVEL.\n\n" +
                        "O cliente " + productView.getName() + " será removido permanentemente do sistema.");
        content.setWrapText(true);

        Text warningText = new Text("Esta ação é IRREVERSÍVEL. ");
        warningText.getStyleClass().add("danger-text");

        Text startText = new Text("\n\nO produto: ");
        startText.getStyleClass().add("common-text");

        Text clientName = new Text(productView.getName());
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

            productRepository.findById(productView.getId()).ifPresentOrElse(product -> {
                productService.deleteProduct(product);
            }, ProductNotFoundException::new);

        }

        // avisa o controller pai
        if (onDataChanged != null) {
            onDataChanged.run();
        }
    }

    public String safe (String str){
        return str.toLowerCase();
    }
}
