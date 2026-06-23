package br.com.midnightsyslabs.flow_control.ui.controller.form;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.config.Constants;
import br.com.midnightsyslabs.flow_control.ui.utils.MaskUtils;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
import br.com.midnightsyslabs.flow_control.ui.utils.EmailUtils;
import br.com.midnightsyslabs.flow_control.service.ClientService;
import br.com.midnightsyslabs.flow_control.repository.CityRepository;
import br.com.midnightsyslabs.flow_control.domain.entity.partner.City;
import br.com.midnightsyslabs.flow_control.exception.ClientNotFoundException;
import br.com.midnightsyslabs.flow_control.exception.IllegalEmailArgumentException;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ContextMenu;

@Controller
public class ClientFormController {

    private final CityRepository cityRepository;

    private final ClientService clientService;

    // private final ContextMenu citySuggestions;
    private City selectedCity;

    private ContextMenu citySuggestions;

    @FXML
    private TextField nameField;

    @FXML
    private Label documentLabel;

    @FXML
    private TextField documentField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> partnerCategoryComboBox;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField cityField;

    private Runnable onDataChanged;

    public ClientFormController(
            ClientService clientService,
            CityRepository cityRepository) {
        this.clientService = clientService;
        this.cityRepository = cityRepository;

    }

    @FXML
    public void initialize() {

        citySuggestions = new ContextMenu();
        var cities = cityRepository.findAll();

        setupCityAutocomplete(cities, citySuggestions);

        partnerCategoryComboBox.getItems().addAll(Constants.PARTNER_CATEGORY);
        partnerCategoryComboBox.getSelectionModel().selectFirst();

        setupDocumentFieldBehavior();

        documentField.setTextFormatter(MaskUtils.cpfMask());
        phoneField.setTextFormatter(MaskUtils.phoneMask());

        emailField.setTextFormatter(EmailUtils.emailFormatter());

    }

    private void setupCityAutocomplete(List<City> cities, ContextMenu citySuggestions) {
        cityField.textProperty().addListener((obs, oldText, newText) -> {

            if (newText == null || newText.length() < 2) {
                citySuggestions.hide();
                return;
            }

            List<MenuItem> suggestions = cities.stream()
                    .filter(c -> c.getName().toLowerCase()
                            .contains(newText.toLowerCase()))
                    .limit(10)
                    .map(city -> {
                        MenuItem item = new MenuItem(city.getName());
                        item.setOnAction(e -> {
                            this.selectedCity = city;
                            cityField.setText(city.getName());
                            citySuggestions.hide();
                        });
                        return item;
                    })
                    .collect(Collectors.toList());

            if (suggestions.isEmpty()) {
                citySuggestions.hide();
            } else {
                citySuggestions.getItems().setAll(suggestions);
                if (!citySuggestions.isShowing()) {
                    citySuggestions.show(cityField,
                            javafx.geometry.Side.BOTTOM, 0, 0);
                }
            }
        });
    }

    private void setupDocumentFieldBehavior() {

        partnerCategoryComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {

            if (newValue == null)
                return;

            if (newValue.equalsIgnoreCase(Constants.PERSONAL)) {
                documentLabel.setText("CPF");
                documentField.setPromptText("999.999.999-99");
                documentField.setTextFormatter(MaskUtils.cpfMask());

            } else {
                documentLabel.setText("CNPJ");
                documentField.setPromptText("99.999.999/0001-99");
                documentField.setTextFormatter(MaskUtils.cnpjMask());
            }

            documentField.clear();
        });
    }

    @FXML
    public void onSave() {
        try {

            if (nameField.getText().isEmpty() || selectedCity == null || partnerCategoryComboBox.getValue() == null) {
                UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                        "Por favor, preencha o nome, selecione uma cidade e a categoria de cliente.");
                return;
            }

            String document = documentField.getText().replaceAll("\\D", "");
            String phone = phoneField.getText().replaceAll("\\D", "");

            clientService.saveClient(
                    nameField.getText(),
                    document,
                    phone,
                    emailField.getText(),
                    selectedCity,
                    partnerCategoryComboBox.getValue());

            if (onDataChanged != null) {
                onDataChanged.run();
            }

        } catch (IllegalEmailArgumentException e) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Erro de email", e.getMessage());
            return;
        }

        catch (IllegalArgumentException e) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Dados Inválidos", e.getMessage());
            return;
        }

        catch (DataIntegrityViolationException e) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro de Integridade de Dados",
                    "O CPF, CNPJ, Telefone ou E-mail já existe no banco de dados!");
            return;
        }

        catch (ClientNotFoundException e) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Cliente não encontrado",
                    e.getMessage());
            return;
        }

        catch (Exception e) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro ao cadastrar cliente",
                    "Ocorreu um erro ao tentar cadastrar o cliente: " + e.getMessage());
            System.err.println(e.getMessage());
            return;
        }

        close();

        UiUtils.showLabelAlert(Alert.AlertType.INFORMATION, "SUCESSO",
                "Cliente cadastrado com sucesso!");
    }

    @FXML
    private void onCancel() {
        close();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    private void close() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
