package br.com.midnightsyslabs.flow_control.ui.cards;

import java.util.Optional;
import org.springframework.context.ApplicationContext;

import br.com.midnightsyslabs.flow_control.exception.ClientNotFoundException;
import br.com.midnightsyslabs.flow_control.repository.CityRepository;
import br.com.midnightsyslabs.flow_control.repository.partner.CompanyPartnerRepository;
import br.com.midnightsyslabs.flow_control.repository.partner.PersonalPartnerRepository;
import br.com.midnightsyslabs.flow_control.service.ClientService;
import br.com.midnightsyslabs.flow_control.ui.controller.form.edit.ClientEditFormController;
import br.com.midnightsyslabs.flow_control.ui.utils.MaskUtils;
import br.com.midnightsyslabs.flow_control.view.ClientView;
import br.com.midnightsyslabs.flow_control.view.PartnerCategory;
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
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ClientCard extends StackPane implements Card {

    private ApplicationContext context;

    private final ClientService clientService;

    private ClientView cView;

    private Runnable onDataChanged;

    // VBox Principal (CONTENT)
    private VBox contentVBox = new VBox(14);
    // HEADER COM BOTÕES À DIREITA -->
    private HBox headerBox = new HBox(12);
    // PARTE DO HEADER PARA INFO DO NOME E DA CATEGORIA
    private VBox nameAndTypeBox = new VBox(2);
    // ACTION BUTTONS (ALINHADOS À DIREITA) -->
    private HBox actionButtonsBox = new HBox(-15);
    // CORPO DO CARD INFORMAÇÕES SOBRE OS CLIENTES
    private VBox clientInformations = new VBox(6);

    private Label lblName = new Label();
    private Label lblSubtitle = new Label();
    private Label lblPhone = new Label();
    private Label lblEmail = new Label();
    private Label lblDocument = new Label();
    private Label lblCity = new Label();
    private ImageView imgType = new ImageView();
    private Button btnEdit;
    private Button btnDelete;
    private StackPane iconContainer = new StackPane();

    public ClientCard(ClientView cView, Runnable onDataChanged, ClientService clientService,
            ApplicationContext context) {
        this.cView = cView;
        this.onDataChanged = onDataChanged;
        this.clientService = clientService;
        this.context = context;
        mountCard();
    }

    @Override
    public void mountCard() {

        this.setPrefWidth(330);
        this.setPrefHeight(170);
        this.getStyleClass().add("client-card");

        lblName.setText(cView.getName());
        lblName.getStyleClass().add("client-name");

        String document = cView.getCategory() == PartnerCategory.PERSONAL
                ? "CPF: " + MaskUtils.applyMask(cView.getDocument(), "###.###.###-##")
                : "CNPJ: " + MaskUtils.applyMask(cView.getDocument(), "##.###.###/####-##");

        lblDocument.setText(document);
        lblPhone.setText("Tel: " + MaskUtils.applyMask(cView.getPhone(), "(##) #####-####"));
        lblEmail.setText("Email: " + cView.getEmail());
        lblCity.setText("Cidade: " + cView.getCity());

        if (cView.getCategory() == PartnerCategory.COMPANY) {
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
        imgType.setFitWidth(22);
        imgType.setFitHeight(22);
        imgType.setPreserveRatio(true);

        iconContainer.getStyleClass().add("icon-container");
        iconContainer.setPrefWidth(42);
        iconContainer.setPrefHeight(42);

        btnEdit = createIconButton("btn-action-edit", "icon-edit", "/images/edit.png");
        if (cView.isConfirmed()) {
            btnDelete = createIconButton("btn-action-delete", "icon-delete-disable", "/images/delete.png");
            btnDelete.setDisable(true);
        } else {
            btnDelete = createIconButton("btn-action-delete", "icon-delete", "/images/delete.png");
        }

        btnEdit.setOnAction(event -> {
            editClient();
        });

        btnDelete.setOnAction(event -> {
            deleteClient();
        });

        HBox.setHgrow(nameAndTypeBox, Priority.ALWAYS);

        actionButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        headerBox.setAlignment(Pos.CENTER_LEFT);

        clientInformations.getChildren().addAll(lblDocument, lblPhone, lblEmail, lblCity);

        contentVBox.setPadding(new Insets(16, 0, 16, 16));

        iconContainer.getChildren().add(imgType);
        nameAndTypeBox.getChildren().addAll(lblName, lblSubtitle);
        actionButtonsBox.getChildren().addAll(btnEdit, btnDelete);
        headerBox.getChildren().addAll(iconContainer, nameAndTypeBox, actionButtonsBox);
        contentVBox.getChildren().addAll(headerBox, clientInformations);
        this.getChildren().add(contentVBox);

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

    private void editClient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/client-edit-form.fxml"));

            var controller = new ClientEditFormController(
                    context.getBean(CompanyPartnerRepository.class),
                    context.getBean(PersonalPartnerRepository.class),
                    context.getBean(ClientService.class),
                    context.getBean(CityRepository.class));

            controller.editClientForm(cView);
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

             if (onDataChanged != null)
                onDataChanged.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteClient() {
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
                        "O cliente " + cView.getName() + " será removido permanentemente do sistema.");
        content.setWrapText(true);

        Text warningText = new Text("Esta ação é IRREVERSÍVEL. ");
        warningText.getStyleClass().add("danger-text");

        Text startText = new Text("\n\nO cliente: ");
        startText.getStyleClass().add("common-text");

        Text clientName = new Text(cView.getName());
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
            // 👉 CHAME A LÓGICA DE DELETE AQUI
            if (cView.getCategory() == PartnerCategory.PERSONAL) {
                clientService.getPersonalPartner(cView.getId()).ifPresentOrElse(client -> {
                    clientService.deletePersonalClient(client);
                    if (onDataChanged != null)
                        onDataChanged.run();
                }, ClientNotFoundException::new);
            } else {
                clientService.getCompanyPartner(cView.getId()).ifPresentOrElse(client -> {
                    clientService.deleteCompanyClient(client);
                    if (onDataChanged != null)
                        onDataChanged.run();
                }, ClientNotFoundException::new);
            }
        }
    }
}
