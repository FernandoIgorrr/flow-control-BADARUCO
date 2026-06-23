package br.com.midnightsyslabs.flow_control.ui.cards;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import br.com.midnightsyslabs.flow_control.exception.SaleNotFoundException;
import br.com.midnightsyslabs.flow_control.repository.CityRepository;
import br.com.midnightsyslabs.flow_control.repository.partner.CompanyPartnerRepository;
import br.com.midnightsyslabs.flow_control.repository.partner.PartnerRepository;
import br.com.midnightsyslabs.flow_control.repository.partner.PersonalPartnerRepository;
import br.com.midnightsyslabs.flow_control.repository.product.MeasurementUnitRepository;
import br.com.midnightsyslabs.flow_control.service.ClientService;
import br.com.midnightsyslabs.flow_control.service.PurchaseService;
import br.com.midnightsyslabs.flow_control.service.RawMaterialService;
import br.com.midnightsyslabs.flow_control.service.SupplierService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import br.com.midnightsyslabs.flow_control.ui.controller.form.edit.ClientEditFormController;
import br.com.midnightsyslabs.flow_control.ui.controller.form.edit.PurchaseEditFormController;
import br.com.midnightsyslabs.flow_control.view.PurchaseView;
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

public class PurchaseCard extends StackPane implements Card {
    private final PurchaseService purchaseService;

    private final ApplicationContext context;

    private Runnable onDataChanged;

    private PurchaseView pView;

    // VBox Principal (CONTENT)
    private VBox contentVBox = new VBox();

    private Label lblRawMaterialName = new Label();

    private TextField lblPurchaseId = new TextField();

    private Label lblTotalPrice = new Label();

    private Label lblQuantityTitle = new Label();

    private Label lblQuantity = new Label();

    private Label lblSupplierName = new Label();

    private Label lblPricePerUnitTitle = new Label();

    private Label lblUnitPrice = new Label();

    private Label lblDate = new Label();

    private Label lblNote = new Label();

    private ImageView imgType = new ImageView();

    private Button btnDelete = new Button();

    private Button btnConfirm = new Button();

    private VBox buttonsBox = new VBox();

    private StackPane iconContainer = new StackPane();;

    private Button btnEdit1;

    private Button btnEdit2;

    public PurchaseCard(PurchaseView pView, Runnable onDataChanged, PurchaseService purchaseService,
            ApplicationContext context) {
        this.purchaseService = purchaseService;
        this.pView = pView;
        this.onDataChanged = onDataChanged;
        this.context = context;
        mountCard();
    }

    @Override
    public void mountCard() {

        this.setMaxWidth(Double.MAX_VALUE);
        this.setPrefHeight(150);
        this.getStyleClass().add("client-card");

        imgType.setImage(new Image(
                getClass().getResourceAsStream("/images/icon-park-outline--milk.png")));
        imgType.setFitHeight(22);
        imgType.setFitHeight(22);
        imgType.setPreserveRatio(true);
        ;
        imgType.getStyleClass().add("green-icon");
        lblRawMaterialName.setText(pView.getRawMaterialName());

        lblPurchaseId.setText("#" + pView.getId().toString());
        lblPurchaseId.setEditable(false);
        lblPurchaseId.setFocusTraversable(false);

        lblTotalPrice.setText(UtilsService.formatPrice(pView.getExpense()));
        lblQuantityTitle.setText(pView.getMeasurementUnitUnit());
        lblSupplierName.setText(pView.getPartnerName());
        lblPricePerUnitTitle.setText("Preço por " + pView.getMeasurementUnitName() + " ("
                + pView.getMeasurementUnitSymbol() + ")");
        lblQuantity.setText(UtilsService.formatQuantity(pView.getQuantity())
                + " " + pView.getMeasurementUnitPluralName() + " ("
                + pView.getMeasurementUnitSymbol() + ")");
        lblUnitPrice.setText(UtilsService.formatPrice(pView.getPricePerUnit()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd 'de' MMMM 'de' yyyy",
                Locale.forLanguageTag("pt-BR"));
        lblDate.setText(pView.getDate().format(formatter));
        lblNote.setText(pView.getNote().isBlank() ? "-" : pView.getNote());

        btnEdit1 = createIconButton("btn-action-edit", "icon-edit", "/images/edit.png");
        btnEdit2 = createIconButton("btn-action-edit", "icon-edit", "/images/edit.png");

        btnEdit1.setOnAction(this::editPurchase);

        btnEdit2.setOnAction(this::editPurchase);

        contentVBox.setPadding(new Insets(0, 22, 12, 22));

        // HEADER (HBox que contém as infos e os botões)
        HBox headerHBox = new HBox();

        // --- ICON AND INFO CONTAINER (Lado Esquerdo) ---
        VBox leftInfoContainer = new VBox(6);

        // Row 1: Icon, Name e ID
        HBox nameAndIdRow = new HBox(6);
        nameAndIdRow.setAlignment(Pos.CENTER_LEFT);
        nameAndIdRow.setPadding(new Insets(12, 0, 0, 0));

        iconContainer.setPrefSize(22, 22);
        iconContainer.getStyleClass().add("icon-container-on-card");
        iconContainer.getChildren().add(imgType);

        lblRawMaterialName.getStyleClass().add("client-name");

        lblPurchaseId = new TextField();
        lblPurchaseId.getStyleClass().addAll("purchase-info-id", "text-field-copy");
        lblPurchaseId.setDisable(true);

        nameAndIdRow.getChildren().addAll(iconContainer, lblRawMaterialName, lblPurchaseId);

        // Row 2: Grid de Detalhes (Fornecedor, Qtd, Preço, Data)
        HBox detailsRow = new HBox();

        detailsRow.getChildren().addAll(
                createDetailColumn("Fornecedor", lblSupplierName, "purchase-info", 200, null),
                createDetailColumn(null, lblQuantity, "purchase-info", 200,
                        lblQuantityTitle),
                createDetailColumn(null, lblUnitPrice, "purchase-info-price", 200,
                        lblPricePerUnitTitle),
                createDetailColumn("Data da compra", lblDate, "purchase-info", 200, null));

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

        // --- TOTAL PRICE (Lado Direito) ---
        VBox totalPriceBox = new VBox();
        totalPriceBox.setAlignment(Pos.TOP_RIGHT);
        totalPriceBox.setPadding(new Insets(12, 0, 0, 0));
        Label totalTitle = new Label("Valor Total");
        lblTotalPrice.getStyleClass().add("total-price-info-blue");

        if (pView.isConfirmed()) {

            // --- SPACER ---
            Region spacerTotalPriceBox = new Region();
            VBox.setVgrow(spacerTotalPriceBox, Priority.ALWAYS);

            totalPriceBox.getChildren().addAll(totalTitle, lblTotalPrice, spacerTotalPriceBox, btnEdit1);
        } else {
            totalPriceBox.getChildren().addAll(totalTitle, lblTotalPrice);
        }

        // --- BUTTONS BOX ---
        buttonsBox.setPadding(new Insets(22, 0, 0, 0));

        btnConfirm = createIconButton("btn-action-confirm", "icon-confirm", "/images/line-md--confirm-circle.png");
        btnDelete = createIconButton("btn-action-delete", "icon-delete", "/images/delete.png");

        btnConfirm.setOnAction(event -> {
            confirmPurchase();
            buttonsBox.setVisible(false);
            buttonsBox.setManaged(false);
        });

        btnDelete.setOnAction(event -> {
            deletePurchase();
        });

        buttonsBox.getChildren().addAll(btnConfirm, btnDelete, btnEdit2);

        // Montagem Final
        headerHBox.getChildren().addAll(leftInfoContainer, spacer, totalPriceBox, buttonsBox);
        contentVBox.getChildren().add(headerHBox);
        this.getChildren().add(contentVBox);

        if (pView.isConfirmed()) {
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

    private void confirmPurchase() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("⚠️ CONFIRMAÇÃO");
        alert.setHeaderText("VOCÊ TEM CERTEZA QUE VAI CONFIRMAR A COMPRA?");

        ButtonType cancelButton = new ButtonType("CANCELAR", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType confirmButton = new ButtonType("CONFIRMAR", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(cancelButton, confirmButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == confirmButton) {
            try {
                purchaseService.findById(pView.getId()).ifPresentOrElse(
                        purchase -> purchaseService.confirmPurchase(purchase), SaleNotFoundException::new);

            } catch (Exception e) {
                Alert alertt = new Alert(Alert.AlertType.ERROR);
                alertt.setTitle("⚠️ ALGO DEU ERRADO");
                alertt.setHeaderText(e.getMessage());
                alertt.show();
            }
        }
    }

    private void editPurchase(ActionEvent event) {
        try {

              Button sourceButton = (Button) event.getSource();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/purchase-edit-form.fxml"));

            var controller = new PurchaseEditFormController(
                    context.getBean(PurchaseService.class),
                    context.getBean(SupplierService.class),
                    context.getBean(PartnerRepository.class));

            controller.editPurchaseForm(pView);
            controller.setOnDataChanged(onDataChanged);
            loader.setControllerFactory(ctr -> controller);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.3;
            double height = screenBounds.getHeight() * 0.6;

            Stage dialog = new Stage();

            dialog.setTitle("Editar Compra");

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

    private void deletePurchase() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("⚠️ CONFIRMAÇÃO DE EXCLUSÃO");
        alert.setHeaderText("VOCÊ TEM CERTEZA?");

        Label content = new Label(
                "Esta ação é IRREVERSÍVEL.\n\n" +
                        "A compra será removida permanentemente do sistema.");
        content.setWrapText(true);

        Text warningText = new Text("Esta ação é IRREVERSÍVEL. ");
        warningText.getStyleClass().add("danger-text");

        Text startText = new Text("\n\nA compra ");
        startText.getStyleClass().add("common-text");

        Text endText = new Text(" será removida permanentemente do sistema.");
        endText.getStyleClass().add("common-text");

        TextFlow textFlow = new TextFlow(warningText, startText, endText);
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
            purchaseService.findById(pView.getId()).ifPresentOrElse(purchase -> {
                purchaseService.deletePurchase(purchase);
                if (onDataChanged != null) {
                    onDataChanged.run();
                }
            }, SaleNotFoundException::new);

        }
    }
}
