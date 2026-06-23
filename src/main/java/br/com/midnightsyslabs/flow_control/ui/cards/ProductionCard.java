package br.com.midnightsyslabs.flow_control.ui.cards;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import org.springframework.context.ApplicationContext;

import br.com.midnightsyslabs.flow_control.exception.ProductionNotFoundException;
import br.com.midnightsyslabs.flow_control.exception.SaleNotFoundException;
import br.com.midnightsyslabs.flow_control.repository.partner.PartnerRepository;
import br.com.midnightsyslabs.flow_control.repository.product.MeasurementUnitRepository;
import br.com.midnightsyslabs.flow_control.service.ProductService;
import br.com.midnightsyslabs.flow_control.service.ProductionService;
import br.com.midnightsyslabs.flow_control.service.PurchaseService;
import br.com.midnightsyslabs.flow_control.service.SupplierService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import br.com.midnightsyslabs.flow_control.ui.controller.form.edit.ProductionEditFormController;
import br.com.midnightsyslabs.flow_control.ui.controller.form.edit.PurchaseEditFormController;
import br.com.midnightsyslabs.flow_control.view.ProductionView;
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
import javafx.scene.control.Separator;
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

public class ProductionCard extends StackPane implements Card {

        private final ProductionService productionService;

        private ApplicationContext context;

        private final Runnable onDataChanged;

        private ImageView imgType, imgType2, imgType3;
        private Label lblProductionDate = new Label();
        private Label lblTotalExpense = new Label();
        private Label lblProductNameDesc = new Label();
        private Label lblProductPrice = new Label();
        private Label lblGrossProductQuantity = new Label();
        private Label lblProductQuantity = new Label();
        private Label lblExpensePerUnit = new Label();
        private Label lblNetIncomePerUnit = new Label();
        private Label lblNetIncome = new Label();
        private Label lblGrossIncome = new Label();
        private Label lblNetIncome2 = new Label();
        private HBox actionButtonsBox = new HBox(-15);
        private Button btnEdit;
        private Button btnDelete;
        private StackPane iconContainer1, iconContainer2, iconContainer3;

        private final ProductionView pView;

        public ProductionCard(ProductionView pView, ProductionService productionService, Runnable onDataChanged, ApplicationContext context) {
                this.pView = pView;
                this.productionService = productionService;
                this.onDataChanged = onDataChanged;
                this.context = context;
                mountCard();
        }

        @Override
        public void mountCard() {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                                "dd 'de' MMMM 'de' yyyy",
                                Locale.forLanguageTag("pt-BR"));

                this.setPrefWidth(500);
                this.setPrefHeight(400);
                this.getStyleClass().add("client-card");

                HBox hContentQuantity = new HBox(8);
                HBox hContentPrice = new HBox(8);
                /*
                 * hContentPrice.setAlignment(Pos.TOP_RIGHT);
                 */
                VBox vContentBlueBox = new VBox(8);
                vContentBlueBox.getStyleClass().add("blue-box");
                Label lblRawMaterialName = new Label(
                                "Leite Utilizado");
                lblRawMaterialName.getStyleClass().add("production-info-date");

                Label lblQuantityUsedTitle = new Label("Quantidade:");

                lblQuantityUsedTitle.getStyleClass().add("production-info-result");

                Label lblQuantityUsed = new Label(UtilsService.formatQuantity(pView.getQuantityUsed()) + " Litros (L)");

                lblQuantityUsed.getStyleClass().add("production-info-result");

                Label lblPricePerUnitTitle = new Label("Preço médio/L:");

                lblPricePerUnitTitle.getStyleClass().add("production-info-result");

                Label lblPricePerUnit = new Label(UtilsService.formatPrice(pView.getAvgRawMaterialUnitPrice()));
                lblPricePerUnit.getStyleClass().add("blue-expense");

                hContentQuantity.getChildren().addAll(lblQuantityUsedTitle, lblQuantityUsed);
                hContentPrice.getChildren().addAll(lblPricePerUnitTitle, lblPricePerUnit);

                vContentBlueBox.setPadding(new Insets(8, 8, 8, 8));
                vContentBlueBox.getChildren().addAll(lblRawMaterialName, hContentQuantity, hContentPrice);

                /*
                 * hContentInfo.getChildren().add(vContentInforItems);
                 * hContentInfo.setMaxWidth(Double.MAX_VALUE);
                 * hContentPrice.getChildren().add(lblTotalPrice);
                 * 
                 * hContent.setPadding(new Insets(8, 8, 8, 8));
                 * 
                 * HBox.setHgrow(hContentInfo, Priority.ALWAYS);
                 * HBox.setHgrow(hContentPrice, Priority.NEVER);
                 * 
                 * hContent.getChildren().addAll(hContentInfo, hContentPrice);
                 */

                /* AQUI COMEÇA O QUANDRADO DEBAIXO */

                lblProductionDate.setText("Produção - " + pView.getDate().format(formatter));

                lblProductNameDesc.setText(
                                pView.getProductName() + " - " + pView.getProductDescription() + " "
                                                + " - " + UtilsService.formatQuantity(pView.getProductQuantity())
                                                + pView.getProductQuantityMeasurementUnit());

                lblProductPrice.setText(UtilsService
                                .formatPrice(pView.getProductCurrentPrice()));

                lblProductPrice.getStyleClass().add("green-net-income-15");

                lblGrossProductQuantity.setText(UtilsService.formatQuantity(pView.getGrossQuantityProduced())
                                + " " + pView.getGrossQuantityMeasurementUnitSymbol() + " (Bruto) => ");

                lblProductQuantity.setText(
                                UtilsService.formatQuantity(pView.getQuantityProduced()) + " Unidades");

                lblProductNameDesc.getStyleClass().add("production-info-result");
                lblGrossProductQuantity.getStyleClass().add("production-info");
                lblProductQuantity.getStyleClass().add("production-info");

                lblNetIncomePerUnit.setText(UtilsService
                                .formatPrice(productionService.productionNetIncomePertUnit(pView)));

                lblGrossIncome.setText(UtilsService.formatPrice(productionService.productionGrossIncome(pView)));

                lblExpensePerUnit.setText(UtilsService
                                .formatPrice(productionService.productionExpensePerProductUnit(pView)));

                lblNetIncome.setText(UtilsService.formatPrice(productionService.productionNetIncome(pView)));
                lblNetIncome2.setText(UtilsService.formatPrice(productionService.productionNetIncome(pView)));

                lblNetIncomePerUnit.getStyleClass().add("green-light-datas-info");
                lblExpensePerUnit.getStyleClass().add("red-light-datas-info");
                lblGrossIncome.getStyleClass().add("green-net-income");
                lblNetIncome.getStyleClass().add("green-light-datas-info");
                lblNetIncome2.getStyleClass().add("green-net-income");
                lblTotalExpense
                                .setText(UtilsService.formatPrice(
                                                productionService.productionTotalExpense(pView)));

                // VBOX PRINCIPAL (CONTENT)
                VBox mainContent = new VBox();
                mainContent.setPadding(new Insets(22, 22, 22, 22));

                // --- HEADER ---
                VBox header = new VBox(8);
                HBox topOfHeader = new HBox(8);
                header.setAlignment(Pos.CENTER_LEFT);
                header.setPadding(new Insets(0));

                iconContainer1 = new StackPane();
                iconContainer1.setPrefSize(22, 22);
                iconContainer1.getStyleClass().add("icon-container-on-card");
                imgType = new ImageView();
                imgType.setImage(new Image(
                                getClass().getResourceAsStream("/images/hugeicons--factory.png")));
                imgType.setFitHeight(22);
                imgType.setFitWidth(22);
                imgType.setPreserveRatio(true);
                iconContainer1.getChildren().add(imgType);

                lblProductionDate.getStyleClass().addAll("production-info-date", "text-field-copy");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                VBox topRightHeader = new VBox();
                topRightHeader.setAlignment(Pos.TOP_LEFT);
                topRightHeader.setPadding(new Insets(22, 0, 0, 0));
                lblTotalExpense.getStyleClass().add("total-price-info-blue");
                topRightHeader.getChildren().addAll(new Label("Custo total da produção"), lblTotalExpense);

                // --- CONTEÚDO (HBox Central) ---
                VBox body = new VBox(24);
                body.setPadding(new Insets(12, 0, 0, 0));

                // LADO DE CIMA (Insumos)
                VBox topSection = new VBox(12);
                // HBox.setHgrow(topSection, Priority.ALWAYS);

                // HBox leftTitleBox = createIconTitleBox("Insumos utilizados", imgType2 = new
                // ImageView());
                // rawMaterialsPurchase = new VBox(6);
                topSection.getChildren().addAll(vContentBlueBox);

                // LADO DE BAIXO (Produto)
                VBox BottomSection = new VBox(12);
                // HBox.setHgrow(BottomSection, Priority.ALWAYS);

                HBox rightTitleBox = createIconTitleBox("Produto Produzido ", imgType3 = new ImageView());

                btnEdit = createIconButton("btn-action-edit", "icon-edit", "/images/edit.png");

                btnDelete = createIconButton("btn-action-delete", "icon-delete", "/images/delete.png");

                btnEdit.setOnAction(this::editProduction);

                btnDelete.setOnAction(this::deleteProduction);

                actionButtonsBox.setAlignment(Pos.CENTER_RIGHT);
                actionButtonsBox.getChildren().addAll(btnEdit, btnDelete);
                topOfHeader.getChildren().addAll(iconContainer1, lblProductionDate, spacer, actionButtonsBox);
                header.getChildren().addAll(topOfHeader, topRightHeader);
                // Caixa Verde Superior
                VBox greenBox1 = new VBox(12);
                greenBox1.getStyleClass().add("green-box");
                greenBox1.setPadding(new Insets(12));

                HBox priceBox = new HBox();
                Label lblPriceTitle = new Label("Preço atual: ");
                lblPriceTitle.getStyleClass().add("production-info");
                priceBox.getChildren().addAll(lblPriceTitle, lblProductPrice);

                HBox qtyBox = new HBox();
                qtyBox.getChildren().addAll(lblGrossProductQuantity, lblProductQuantity);

                Separator sep = new Separator();
                sep.setPrefWidth(120);
                sep.getStyleClass().add("green-separator");

                HBox metricsBox = new HBox(24);

                metricsBox.getChildren().addAll(
                                createMetricVBox("Custo/Unid", lblExpensePerUnit),
                                createMetricVBox("Lucro/Unid", lblNetIncomePerUnit),
                                createMetricVBox("Receita Líq.", lblNetIncome));

                greenBox1.getChildren().addAll(lblProductNameDesc, priceBox, qtyBox, sep, metricsBox);

                // Caixa Verde Inferior (Resultados)
                HBox greenBox2 = new HBox(12);
                greenBox2.getStyleClass().add("green-box2");
                greenBox2.setPadding(new Insets(12));

                greenBox2.getChildren().addAll(
                                createMetricVBox("Receita Bruta Total", lblGrossIncome, "production-info-result"),
                                createMetricVBox("Receita Líquida Total", lblNetIncome2, "production-info-result"));

                BottomSection.getChildren().addAll(rightTitleBox, greenBox1, greenBox2);

                body.getChildren().addAll(topSection, BottomSection);
                mainContent.getChildren().addAll(header, body);
                this.getChildren().add(mainContent);

        }

        private HBox createIconTitleBox(String title, ImageView img) {
                HBox hb = new HBox(12);
                StackPane sp = new StackPane();
                sp.setPrefSize(18, 18);
                sp.getStyleClass().add("icon-container-on-card");
                img.setImage(new Image(
                                getClass().getResourceAsStream("/images/streamline--graph-arrow-increase.png")));
                img.setFitHeight(18);
                img.setFitWidth(18);
                img.setPreserveRatio(true);
                sp.getChildren().add(img);
                Label lbl = new Label(title);
                lbl.getStyleClass().add("label-grey-13-bold");
                hb.getChildren().addAll(sp, lbl);
                return hb;
        }

        private VBox createMetricVBox(String title, Label valueLabel) {
                return createMetricVBox(title, valueLabel, "production-info");
        }

        private VBox createMetricVBox(String title, Label valueLabel, String styleClass) {
                VBox vb = new VBox();
                HBox.setHgrow(vb, Priority.ALWAYS);
                Label t = new Label(title);
                t.getStyleClass().add(styleClass);
                vb.getChildren().addAll(t, valueLabel);
                return vb;
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

        private void editProduction(ActionEvent event) {
                try {

                        Button sourceButton = (Button) event.getSource();

                        FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("/fxml/form/production-edit-form.fxml"));

                        var controller = new ProductionEditFormController(
                                        context.getBean(MeasurementUnitRepository.class),
                                        context.getBean(PurchaseService.class),
                                        context.getBean(ProductService.class),
                                        context.getBean(ProductionService.class)
                                        );

                        controller.editProductionForm(pView);
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

        public void deleteProduction(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("⚠️ CONFIRMAÇÃO DE EXCLUSÃO");
                alert.setHeaderText("VOCÊ TEM CERTEZA?");

                Label content = new Label(
                                "Esta ação é IRREVERSÍVEL.\n\n" +
                                                "A produção será removida permanentemente do sistema.");
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
                        productionService.findById(this.pView.getId()).ifPresentOrElse(production -> {
                                productionService.deleteProduction(production);
                        }, ProductionNotFoundException::new);

                        if (onDataChanged != null) {
                                onDataChanged.run();
                        }

                }
        }
}
 