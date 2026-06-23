package br.com.midnightsyslabs.flow_control.ui.cards;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import org.springframework.context.ApplicationContext;

import br.com.midnightsyslabs.flow_control.domain.entity.spent.VehicleSpent;
import br.com.midnightsyslabs.flow_control.repository.spent.SpentCategoryRepository;
import br.com.midnightsyslabs.flow_control.service.VehicleSpentService;
import br.com.midnightsyslabs.flow_control.service.EmojiService;
import br.com.midnightsyslabs.flow_control.service.SpentService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import br.com.midnightsyslabs.flow_control.service.VehicleService;
import br.com.midnightsyslabs.flow_control.ui.controller.form.edit.SpentEditFormController;
import br.com.midnightsyslabs.flow_control.ui.controller.form.edit.VehicleSpentEditFormController;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
// Crie este controller futuramente para edição se necessário
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class VehicleSpentCard extends StackPane implements Card {

    private final VehicleSpentService vehicleSpentService;
    private final ApplicationContext context;
    private Runnable onDataChanged;
    private VehicleSpent vehicleSpent;

    // VBox Principal (CONTENT)
    private VBox contentVBox = new VBox();
    private Label lblCategoryName = new Label();
    private TextField lblSpentId = new TextField();
    private Label lblTotalPrice = new Label();
    private Label lblVehicleTitle = new Label();
    private Label lblVehicleInfo = new Label();
    private Label lblQuantityTitle = new Label();
    private Label lblQuantity = new Label();
    private Label lblUnitPriceTitle = new Label();
    private Label lblUnitPrice = new Label();
    private Label lblDate = new Label();
    private Label lblNote = new Label();
    private ImageView imgType = new ImageView();

    private Button btnDelete = new Button();
    private Button btnConfirm = new Button();
    private VBox buttonsBox = new VBox();
    private StackPane iconContainer = new StackPane();

    private Button btnEdit1;
    private Button btnEdit2;

    public VehicleSpentCard(VehicleSpent vehicleSpent, Runnable onDataChanged, VehicleSpentService vehicleSpentService,
            ApplicationContext context) {
        this.vehicleSpentService = vehicleSpentService;
        this.vehicleSpent = vehicleSpent;
        this.onDataChanged = onDataChanged;
        this.context = context;
        mountCard();
    }

    @Override
    public void mountCard() {
        this.setMaxWidth(Double.MAX_VALUE);
        this.setPrefHeight(150);
        this.getStyleClass().add("client-card"); // Mantendo seus estilos CSS de cards

        // Alterado o ícone padrão para um ícone de carro/veícul
        if (vehicleSpent.getCategory().getId() == 10) {
            imgType.setImage(new Image(
                    getClass().getResourceAsStream("/images/mdi--petrol.png")));
        } else if (vehicleSpent.getCategory().getId() == 11) {
            imgType.setImage(new Image(
                    getClass().getResourceAsStream("/images/mdi--mechanic.png")));
        } else {
            imgType.setImage(new Image(
                    getClass().getResourceAsStream("/images/mdi--car-lifted-pickup.png")));
        }

        imgType.setFitHeight(22);
        imgType.setFitWidth(22);
        imgType.setPreserveRatio(true);
        imgType.getStyleClass().add("green-icon");

        lblCategoryName.setText(vehicleSpent.getCategory().getName());
        lblSpentId.setText("#" + vehicleSpent.getId().toString());
        lblSpentId.setEditable(false);
        lblSpentId.setFocusTraversable(false);

        // Preço Total = Valor Pago * Quantidade (ou apenas Valor Pago dependendo da sua
        // regra de negócio)
        lblTotalPrice.setText(UtilsService.formatPrice(vehicleSpent.getExpense()));

        lblVehicleTitle.setText("Veículo");
        lblVehicleInfo
                .setText(vehicleSpent.getVehicle().getNumberPlate() + " - " + vehicleSpent.getVehicle().getModel());

        lblQuantityTitle.setText("Quantidade");
        lblQuantity.setText(UtilsService.formatQuantity(vehicleSpent.getQuantity()));

        lblUnitPriceTitle.setText("Valor Unitário");
        lblUnitPrice.setText(UtilsService.formatPrice(vehicleSpent.getAmountPaid()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd 'de' MMMM 'de' yyyy",
                Locale.forLanguageTag("pt-BR"));
        lblDate.setText(vehicleSpent.getDate().format(formatter));
        lblNote.setText(
                vehicleSpent.getNote() == null || vehicleSpent.getNote().isBlank() ? "-" : vehicleSpent.getNote());

        btnEdit1 = createIconButton("btn-action-edit", "icon-edit", "/images/edit.png");
        btnEdit2 = createIconButton("btn-action-edit", "icon-edit", "/images/edit.png");

        btnEdit1.setOnAction(this::editVehicleSpent);
        btnEdit2.setOnAction(this::editVehicleSpent);

        contentVBox.setPadding(new Insets(0, 22, 12, 22));

        // HEADER (HBox principal)
        HBox headerHBox = new HBox();

        // --- CONTAINER DE INFO ESQUERDO ---
        VBox leftInfoContainer = new VBox(6);

        // Row 1: Icon, Categoria e ID
        HBox nameAndIdRow = new HBox(6);
        nameAndIdRow.setAlignment(Pos.CENTER_LEFT);
        nameAndIdRow.setPadding(new Insets(12, 0, 0, 0));

        iconContainer.setPrefSize(22, 22);
        iconContainer.getStyleClass().add("icon-container-on-card");
        iconContainer.getChildren().add(imgType);

        lblCategoryName.getStyleClass().add("client-name");

        lblSpentId = new TextField();
        lblSpentId.getStyleClass().addAll("purchase-info-id", "text-field-copy");
        lblSpentId.setDisable(true);
        lblSpentId.setText("#" + vehicleSpent.getId().toString());

        nameAndIdRow.getChildren().addAll(iconContainer, lblCategoryName, lblSpentId);

        // Row 2: Grid de Detalhes (Veículo, Qtd, Preço, Data)
        HBox detailsRow = new HBox();
        detailsRow.getChildren().addAll(
                createDetailColumn(null, lblVehicleInfo, "purchase-info", 200, lblVehicleTitle),
                createDetailColumn(null, lblQuantity, "purchase-info", 200, lblQuantityTitle),
                createDetailColumn(null, lblUnitPrice, "purchase-info-price", 200, lblUnitPriceTitle),
                createDetailColumn("Data do Gasto", lblDate, "purchase-info", 200, null));

        // Row 3: Observação
        VBox noteBox = new VBox();
        Label noteTitle = new Label("Observação");
        noteTitle.getStyleClass().add("purchase-info-title");
        lblNote.getStyleClass().add("purchase-info");
        noteBox.getChildren().addAll(noteTitle, lblNote);

        leftInfoContainer.getChildren().addAll(nameAndIdRow, detailsRow, noteBox);

        // --- SPACER ---
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- VALOR TOTAL (Lado Direito) ---
        VBox totalPriceBox = new VBox();
        totalPriceBox.setAlignment(Pos.TOP_RIGHT);
        totalPriceBox.setPadding(new Insets(12, 0, 0, 0));
        Label totalTitle = new Label("Valor Total");
        lblTotalPrice.getStyleClass().add("total-price-info-blue");

        if (vehicleSpent.isConfirmed()) {
            Region spacerTotalPriceBox = new Region();
            VBox.setVgrow(spacerTotalPriceBox, Priority.ALWAYS);
            totalPriceBox.getChildren().addAll(totalTitle, lblTotalPrice, spacerTotalPriceBox, btnEdit1);
        } else {
            totalPriceBox.getChildren().addAll(totalTitle, lblTotalPrice);
        }

        // --- BOX DE BOTÕES ---
        buttonsBox.setPadding(new Insets(22, 0, 0, 0));

        btnConfirm = createIconButton("btn-action-confirm", "icon-confirm", "/images/line-md--confirm-circle.png");
        btnDelete = createIconButton("btn-action-delete", "icon-delete", "/images/delete.png");

        btnConfirm.setOnAction(event -> {
            confirmVehicleSpent();
            buttonsBox.setVisible(false);
            buttonsBox.setManaged(false);
        });

        btnDelete.setOnAction(event -> {
            deleteVehicleSpent();
        });

        buttonsBox.getChildren().addAll(btnConfirm, btnDelete, btnEdit2);

        // Montagem Final do Componente
        headerHBox.getChildren().addAll(leftInfoContainer, spacer, totalPriceBox, buttonsBox);
        contentVBox.getChildren().add(headerHBox);
        this.getChildren().add(contentVBox);

        if (vehicleSpent.isConfirmed()) {
            buttonsBox.setVisible(false);
            buttonsBox.setManaged(false);
        }
    }

    private VBox createDetailColumn(String titleText, Label valueLabel, String valueStyle, double width,
            Label dynamicTitle) {
        VBox vbox = new VBox();
        vbox.setPrefWidth(width);

        Label title = (dynamicTitle != null) ? dynamicTitle : new Label(titleText);
        title.getStyleClass().add("purchase-info-title");

        valueLabel.getStyleClass().add(valueStyle);
        vbox.getChildren().addAll(title, valueLabel);
        return vbox;
    }

    private Button createIconButton(String btnStyle, String iconStyle, String imgPath) {
        Button btn = new Button();
        btn.getStyleClass().add(btnStyle);

        StackPane iconPane = new StackPane();
        iconPane.setPrefSize(28, 28);

        try {
            ImageView img = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
            img.setFitHeight(28);
            img.setFitWidth(28);
            img.setPreserveRatio(true);
            img.getStyleClass().add(iconStyle);
            iconPane.getChildren().add(img);
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem: " + imgPath);
        }

        btn.setGraphic(iconPane);
        return btn;
    }

    private void confirmVehicleSpent() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("⚠️ CONFIRMAÇÃO");
        alert.setHeaderText("VOCÊ TEM CERTEZA QUE DESEJA CONFIRMAR ESTE GASTO DE VEÍCULO?");

        ButtonType cancelButton = new ButtonType("CANCELAR", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType confirmButton = new ButtonType("CONFIRMAR", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(cancelButton, confirmButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == confirmButton) {
            try {
                vehicleSpentService.findById(vehicleSpent.getId()).ifPresentOrElse(
                        vs -> vehicleSpentService.confirmVehicleSpent(vs),
                        () -> {
                            throw new RuntimeException("Gasto não encontrado!");
                        });
                if (onDataChanged != null)
                    onDataChanged.run();
            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("⚠️ ALGO DEU ERRADO");
                errorAlert.setHeaderText(e.getMessage());
                errorAlert.show();
            }
        }
    }

    private void editVehicleSpent(ActionEvent event) {
        try {

            Button sourceButton = (Button) event.getSource();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/vehicle-spent-edit-form.fxml"));

            var controller = new VehicleSpentEditFormController(
                    context.getBean(VehicleSpentService.class),
                    context.getBean(VehicleService.class),
                    context.getBean(SpentCategoryRepository.class));

            controller.editVehicleSpentForm(vehicleSpent);
            controller.setOnDataChanged(onDataChanged);
            loader.setControllerFactory(ctr -> controller);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.3;
            double height = screenBounds.getHeight() * 0.6;

            Stage dialog = new Stage();

            dialog.setTitle("Editar Despesa");

            dialog.setScene(new Scene(loader.load(), width, height));

            Stage mainStage = (Stage) sourceButton.getScene().getWindow();

            dialog.initOwner(mainStage);
            dialog.initModality(Modality.WINDOW_MODAL);

            dialog.setResizable(false);
            // stage.showAndWait();

            ColorAdjust darken = new ColorAdjust();
            darken.setBrightness(-0.8);
            mainStage.getScene().getRoot().setEffect(darken);

            dialog.setOnHidden(e -> mainStage.getScene().getRoot().setEffect(null));

            dialog.showAndWait();

            if (onDataChanged != null)
                onDataChanged.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteVehicleSpent() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("⚠️ CONFIRMAÇÃO DE EXCLUSÃO");
        alert.setHeaderText("VOCÊ TEM CERTEZA?");

        Text warningText = new Text("Esta ação é IRREVERSÍVEL. ");
        warningText.getStyleClass().add("danger-text");

        Text startText = new Text("\n\nO gasto do veículo ");
        startText.getStyleClass().add("common-text");

        Text endText = new Text(" será removido permanentemente do sistema.");
        endText.getStyleClass().add("common-text");

        TextFlow textFlow = new TextFlow(warningText, startText, endText);
        textFlow.setMaxWidth(420);

        alert.getDialogPane().setContent(textFlow);

        ButtonType cancelButton = new ButtonType("CANCELAR", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType deleteButton = new ButtonType("DELETAR", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(cancelButton, deleteButton);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/alert-danger.css").toExternalForm());
        dialogPane.getStyleClass().add("danger-alert");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == deleteButton) {
            vehicleSpentService.findById(vehicleSpent.getId()).ifPresentOrElse(vs -> {
                vehicleSpentService.deleteVehicleSpent(vs);
                if (onDataChanged != null) {
                    onDataChanged.run();
                }
            }, () -> {
                throw new RuntimeException("Gasto não encontrado!");
            });
        }
    }
}
