package br.com.midnightsyslabs.flow_control.ui.controller.card;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.domain.entity.raw_material.RawMaterial;
import br.com.midnightsyslabs.flow_control.exception.ProductNotFoundException;
import br.com.midnightsyslabs.flow_control.repository.RawMaterialRepository;
import br.com.midnightsyslabs.flow_control.repository.product.ProductRepository;
import br.com.midnightsyslabs.flow_control.service.ProductService;
import br.com.midnightsyslabs.flow_control.service.RawMaterialService;
import br.com.midnightsyslabs.flow_control.ui.controller.form.edit.ProductEditFormController;
import br.com.midnightsyslabs.flow_control.ui.controller.form.edit.RawMaterialEditFormController;
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
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

@Controller
@Scope("prototype")
public class RawMaterialCardController {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private RawMaterialService rawMaterialService;

    private Runnable onDataChanged; // callback

    private RawMaterial rawMaterial;

    @FXML
    private Label lblName;
    @FXML
    private Label lblDescription;
   
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    public void setRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterial = rawMaterial;

        lblName.setText(this.rawMaterial.getName());
        lblDescription.setText(this.rawMaterial.getDescription());
       

    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    public void onEdit() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/raw-material-edit-form.fxml"));

            var controller = new RawMaterialEditFormController(
                    context.getBean(RawMaterialService.class)
                    );

            controller.editRawMaterialForm(this.rawMaterial);
            loader.setControllerFactory(ctr -> controller);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.2;
            double height = screenBounds.getHeight() * 0.4;

            Stage dialog = new Stage();

            dialog.setTitle("Editar Matéria-Prima");

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
                        "A matéria-prima " + this.rawMaterial.getName() + " será removido permanentemente do sistema.");
        content.setWrapText(true);

        Text warningText = new Text("Esta ação é IRREVERSÍVEL. ");
        warningText.getStyleClass().add("danger-text");

        Text startText = new Text("\n\nA matéria-prima: ");
        startText.getStyleClass().add("common-text");

        Text clientName = new Text(this.rawMaterial.getName());
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

            rawMaterialService.deleteRawMaterial(this.rawMaterial);

        }

        // avisa o controller pai
        if (onDataChanged != null) {
            onDataChanged.run();
        }
    }
}
