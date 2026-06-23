package br.com.midnightsyslabs.flow_control.ui.cards;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.midnightsyslabs.flow_control.domain.entity.employee.EmployeePayment;
import br.com.midnightsyslabs.flow_control.exception.SpentNotFoundException;
import br.com.midnightsyslabs.flow_control.service.EmojiService;
import br.com.midnightsyslabs.flow_control.service.EmployeeService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class EmployeePaymentCard extends StackPane implements Card {

    private final EmojiService emojiService;
    private final EmployeeService employeeService;
    private Runnable onDataChanged;
    private EmployeePayment ep;

    // Componentes de UI
    private final Label lblSpentCategory = new Label();
    private final Label lblEmployeeName = new Label();
    private final Label lblDate = new Label();
    private final Label lblTotalPaid = new Label();
    private final ImageView imgType = new ImageView();

    private Button btnDelete;

    private Button btnConfirm;

    private VBox buttonsBox = new VBox();

    public EmployeePaymentCard(
            EmployeePayment ep,
            Runnable onDataChanged,
            EmployeeService employeeService,
            EmojiService emojiService) {
        this.ep = ep;
        this.onDataChanged = onDataChanged;
        this.employeeService = employeeService;
        this.emojiService = emojiService;

        mountCard();
    }

    @Override
    public void mountCard() {

        // Configuração do Root (StackPane)
        this.getChildren().clear();
        this.setMaxWidth(Double.MAX_VALUE);
        this.setPrefHeight(50);
        this.getStyleClass().add("client-card");

        lblSpentCategory.setText(emojiService.getEmoji(ep.getSpentCategory().getId()) + " "
                + ep.getSpentCategory().getName());

        imgType.setImage(new Image(
                getClass().getResourceAsStream("/images/lucide--calendar.png")));
        imgType.getStyleClass().add("grey-icon");
        lblEmployeeName.setText(ep.getEmployee().getName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd 'de' MMMM 'de' yyyy",
                Locale.forLanguageTag("pt-BR"));

        lblDate.setText(ep.getDate().format(formatter));
        lblTotalPaid.setText(UtilsService.formatPrice(ep.getExpense()));

        // VBox Principal (CONTENT)
        VBox contentVBox = new VBox(12);
        contentVBox.setPadding(new Insets(0, 22, 22, 22));

        // HEADER (HBox)
        HBox headerHBox = new HBox(8);
        headerHBox.setAlignment(Pos.CENTER_LEFT);

        // --- Lado Esquerdo (Informações) ---
        VBox leftInfoBox = new VBox(8);
        leftInfoBox.setPadding(new Insets(22, 0, 0, 0));

        // Row 1: Categoria e Nome do Funcionário
        HBox nameRow = new HBox(12);

        lblSpentCategory.getStyleClass().add("employee-category-box");

        lblEmployeeName.getStyleClass().add("production-info-date");

        nameRow.getChildren().addAll(lblSpentCategory, lblEmployeeName);

        // Row 2: Ícone e Data
        HBox dateRow = new HBox(8);

        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(18, 18);
        iconContainer.getStyleClass().add("icon-container-on-card");

        imgType.setImage(new Image(getClass().getResourceAsStream("/images/lucide--calendar.png"))); // Ajuste o caminho
                                                                                                     // do
                                                                                                     // ícone
        imgType.setFitWidth(18);
        imgType.setFitHeight(18);
        imgType.setPreserveRatio(true);
        iconContainer.getChildren().add(imgType);

        lblDate.getStyleClass().add("purchase-info-title");

        dateRow.getChildren().addAll(iconContainer, lblDate);

        leftInfoBox.getChildren().addAll(nameRow, dateRow);

        // --- SPACER ---
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- Lado Direito (Valores) ---
        VBox rightInfoBox = new VBox();
        rightInfoBox.setAlignment(Pos.TOP_RIGHT);
        rightInfoBox.setPadding(new Insets(22, 0, 0, 0));

        Label lblTitlePaid = new Label("Salário pago");

        lblTotalPaid.getStyleClass().add("total-price-info-blue");

        rightInfoBox.getChildren().addAll(lblTitlePaid, lblTotalPaid);

        // --- BUTTONS BOX ---
        buttonsBox.setPadding(new Insets(22, 0, 0, 0));

        btnConfirm = createIconButton("btn-action-confirm", "icon-confirm", "/images/line-md--confirm-circle.png");
        btnDelete = createIconButton("btn-action-delete", "icon-delete", "/images/delete.png");

        btnConfirm.setOnAction(event -> {
            confirmEmployeePayment();
            buttonsBox.setVisible(false);
            buttonsBox.setManaged(false);
        });

        btnDelete.setOnAction(event -> {
            deleteEmployeePayment();
        });

        buttonsBox.getChildren().addAll(btnConfirm, btnDelete);

        // Montagem final
        headerHBox.getChildren().addAll(leftInfoBox, spacer, rightInfoBox, buttonsBox);
        contentVBox.getChildren().add(headerHBox);
        this.getChildren().add(contentVBox);

        if (ep.isConfirmed()) {
            buttonsBox.setVisible(false);
            buttonsBox.setManaged(false);
        }
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

    private void confirmEmployeePayment() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("⚠️ CONFIRMAÇÃO");
        alert.setHeaderText("VOCÊ TEM CERTEZA QUE VAI CONFIRMAR A COMPRA?");

        ButtonType cancelButton = new ButtonType("CANCELAR", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType confirmButton = new ButtonType("CONFIRMAR", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(cancelButton, confirmButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == confirmButton) {
            try {

                employeeService.confirmEmployeePayment(ep);

            } catch (Exception e) {
                Alert alertt = new Alert(Alert.AlertType.ERROR);
                alertt.setTitle("⚠️ ALGO DEU ERRADO");
                alertt.setHeaderText(e.getMessage());
                alertt.show();
            }
        }
    }

    private void deleteEmployeePayment() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("⚠️ CONFIRMAÇÃO DE EXCLUSÃO");
        alert.setHeaderText("VOCÊ TEM CERTEZA?");

        Label content = new Label(
                "Esta ação é IRREVERSÍVEL.\n\n" +
                        "A DESPESA será removida permanentemente do sistema.");
        content.setWrapText(true);

        Text warningText = new Text("Esta ação é IRREVERSÍVEL. ");
        warningText.getStyleClass().add("danger-text");

        Text startText = new Text("\n\nA despesa ");
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
            try {
                employeeService.deleteEmployeePayment(ep);

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
}
