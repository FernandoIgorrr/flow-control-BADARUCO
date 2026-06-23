package br.com.midnightsyslabs.flow_control.ui.cards;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.midnightsyslabs.flow_control.dto.SaleDTO;
import br.com.midnightsyslabs.flow_control.exception.SaleNotFoundException;
import br.com.midnightsyslabs.flow_control.repository.sale.SaleRepository;
import br.com.midnightsyslabs.flow_control.service.SaleService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import br.com.midnightsyslabs.flow_control.view.SaleProductView;
import br.com.midnightsyslabs.flow_control.view.SaleView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class SaleCard extends StackPane implements Card {

    private final SaleService saleService;

    private SaleDTO sDTO;

    private Runnable onDataChanged;

    private Label lblClientName = new Label();

    private Label lblTotalRevenue = new Label();

    private ImageView imgType = new ImageView();

    private Label lblDate = new Label();

    private FlowPane productsContainer = new FlowPane();

    private Button btnConfirm;

    private Button btnDelete;

    private VBox buttonsBox = new VBox();

    public SaleCard(SaleDTO sDTO, Runnable onDataChanged, SaleService saleService) {
        this.sDTO = sDTO;
        this.onDataChanged = onDataChanged;
        this.saleService = saleService;

        mountCard();
    }

    @Override
    public void mountCard() {

        // Limpeza para remontagem
        this.getChildren().clear();
        this.getStyleClass().add("client-card");
        this.setMaxWidth(Double.MAX_VALUE);
        this.setPrefHeight(150);

        lblClientName.setText(sDTO.getClientName());
        lblTotalRevenue.setText(UtilsService.formatPrice(sDTO.getRevenue()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd 'de' MMMM 'de' yyyy",
                Locale.forLanguageTag("pt-BR"));

        lblDate.setText(sDTO.getDate().format(formatter));

        /* DETALHES DOS PRODUTOS VENDIDOS */

        productsContainer.getChildren().clear();

        productsContainer.setHgap(12);
        productsContainer.setVgap(12);

        // ESSENCIAL: wrap acompanha a largura do container
        productsContainer.prefWrapLengthProperty().bind(
                productsContainer.widthProperty());

        for (var product : sDTO.getSaleProductsView()) {
            HBox productBox = createProductBox(product);
            productsContainer.getChildren().add(productBox);
        }

        // --- CONTENT VBOX ---
        VBox contentVBox = new VBox(12);
        contentVBox.setPadding(new Insets(0, 22, 22, 22));

        // --- HEADER HBOX ---
        HBox headerHBox = new HBox(8);
        headerHBox.setAlignment(Pos.CENTER_LEFT);

        // Lado Esquerdo: Info do Cliente e Data
        VBox leftInfoBox = new VBox(8);
        leftInfoBox.setPadding(new Insets(22, 0, 0, 0));

        lblClientName.setText(sDTO.getClientName());
        lblClientName.getStyleClass().add("production-info-date");

        HBox dateRow = new HBox(8);
        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().add("icon-container-on-card");
        iconContainer.setPrefSize(18, 18);

        imgType.setImage(new Image(
                getClass().getResourceAsStream("/images/lucide--calendar.png")));
        imgType.getStyleClass().add("grey-icon");
        imgType.setFitWidth(18);
        imgType.setFitHeight(18);
        imgType.setPreserveRatio(true);
        iconContainer.getChildren().add(imgType);

        lblDate.getStyleClass().add("purchase-info-title");
        dateRow.getChildren().addAll(iconContainer, lblDate);

        leftInfoBox.getChildren().addAll(lblClientName, dateRow);

        // --- SPACER ---
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- TOP RIGHT: Valor Total ---
        VBox totalValueBox = new VBox();
        totalValueBox.setAlignment(Pos.TOP_RIGHT);
        totalValueBox.setPadding(new Insets(22, 0, 0, 0));

        Label totalTitle = new Label("Valor Total");
        lblTotalRevenue.setText(UtilsService.formatPrice(sDTO.getRevenue()));
        lblTotalRevenue.getStyleClass().add("total-price-info");
        totalValueBox.getChildren().addAll(totalTitle, lblTotalRevenue);

        // --- BUTTONS ---
        buttonsBox = new VBox();
        buttonsBox.setPadding(new Insets(22, 0, 0, 0));

        btnConfirm = createIconButton("btn-action-confirm", "icon-confirm", "/images/line-md--confirm-circle.png");
        btnDelete = createIconButton("btn-action-delete", "icon-delete", "/images/delete.png");

        btnConfirm.setOnAction(e -> {
            confirmSale();
            buttonsBox.setVisible(false);
            buttonsBox.setManaged(false);
        });

        btnDelete.setOnAction(e -> {
            deleteSale();
        });

        buttonsBox.getChildren().addAll(btnConfirm, btnDelete);

        // Montagem do Header
        headerHBox.getChildren().addAll(leftInfoBox, spacer, totalValueBox, buttonsBox);

        // --- SEPARATOR ---
        Separator sep = new Separator();
        sep.setPrefWidth(120);

        // --- PRODUCTS CONTAINER (FlowPane) ---
        productsContainer.setHgap(12);
        productsContainer.setVgap(12);
        productsContainer.setPrefWidth(Double.MAX_VALUE);
        productsContainer.getStyleClass().add("products-container");

        // Montagem final do conteúdo
        contentVBox.getChildren().addAll(headerHBox, sep, productsContainer);
        this.getChildren().add(contentVBox);

        if (sDTO.isConfirmed()) {
            buttonsBox.setVisible(false);
            buttonsBox.setManaged(false);

        }

    }

    private HBox createProductBox(SaleProductView saleProductView) {
        HBox box = new HBox(8);
        VBox vbox = new VBox(8);
        VBox vboxTotalPricePerProduct = new VBox();
        box.getStyleClass().add("green-box");

        // cada item ocupa 50% do FlowPane
        box.prefWidthProperty().bind(
                productsContainer.widthProperty()
                        .subtract(productsContainer.getHgap() + 10) // espaço entre colunas
                        .divide(2));
        box.setPadding(new Insets(8, 8, 8, 8));
        Label lblNamePlusDescription = new Label(
                saleProductView.getProductName() + " | " + saleProductView.getProductDescription());
        Label lblQtyXPrice = new Label(
                UtilsService.formatQuantity(saleProductView.getProductQuantitySold()) + " X " +
                        UtilsService.formatPrice(saleProductView.getProductPriceOnSaleDate()));
        Label lblTotalPricePerProduct = new Label(
                UtilsService.formatPrice(
                        saleProductView.getProductPriceOnSaleDate()
                                .multiply(saleProductView.getProductQuantitySold())));
        lblTotalPricePerProduct.getStyleClass().add("green-net-income");
        vbox.getChildren().addAll(lblNamePlusDescription, lblQtyXPrice);

        vboxTotalPricePerProduct.setAlignment(Pos.CENTER_RIGHT);
        vboxTotalPricePerProduct.getChildren().add(lblTotalPricePerProduct);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        box.getChildren().addAll(vbox, spacer, vboxTotalPricePerProduct);

        return box;
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

    public void confirmSale() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("⚠️ CONFIRMAÇÃO");
        alert.setHeaderText("VOCÊ TEM CERTEZA QUE VAI CONFIRMAR A VENDA?");

        ButtonType cancelButton = new ButtonType("CANCELAR", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType confirmButton = new ButtonType("CONFIRMAR", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(cancelButton, confirmButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == confirmButton) {
            try {
                saleService.findById(sDTO.getId()).ifPresentOrElse(
                        sale -> saleService.confirmSale(sale), SaleNotFoundException::new);
                if (onDataChanged != null) {
                    onDataChanged.run();
                }
            } catch (Exception e) {
                Alert alertt = new Alert(Alert.AlertType.ERROR);
                alertt.setTitle("⚠️ ALGO DEU ERRADO");
                alertt.setHeaderText(e.getMessage());
                alertt.show();
            }

        }
    }

    public void deleteSale() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("⚠️ CONFIRMAÇÃO DE EXCLUSÃO");
        alert.setHeaderText("VOCÊ TEM CERTEZA?");

        Label content = new Label(
                "Esta ação é IRREVERSÍVEL.\n\n" +
                        "A venda será removida permanentemente do sistema.");
        content.setWrapText(true);

        Text warningText = new Text("Esta ação é IRREVERSÍVEL. ");
        warningText.getStyleClass().add("danger-text");

        Text startText = new Text("\n\nA venda ");
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
            saleService.findById(this.sDTO.getId()).ifPresentOrElse(sale -> {
                saleService.deleteSale(sale);
            }, SaleNotFoundException::new);

            if (onDataChanged != null) {
                onDataChanged.run();
            }

        }
    }
}
