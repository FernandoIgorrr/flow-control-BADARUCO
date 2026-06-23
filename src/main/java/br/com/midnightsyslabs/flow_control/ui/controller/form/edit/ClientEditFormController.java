package br.com.midnightsyslabs.flow_control.ui.controller.form.edit;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.dao.DataIntegrityViolationException;

import br.com.midnightsyslabs.flow_control.config.Constants;
import br.com.midnightsyslabs.flow_control.ui.utils.MaskUtils;
import br.com.midnightsyslabs.flow_control.view.ClientView;
import br.com.midnightsyslabs.flow_control.view.PartnerCategory;
import br.com.midnightsyslabs.flow_control.ui.utils.EmailUtils;
import br.com.midnightsyslabs.flow_control.service.ClientService;
import br.com.midnightsyslabs.flow_control.repository.CityRepository;
import br.com.midnightsyslabs.flow_control.domain.entity.partner.City;
import br.com.midnightsyslabs.flow_control.exception.ClientNotFoundException;
import br.com.midnightsyslabs.flow_control.exception.IllegalEmailArgumentException;
import br.com.midnightsyslabs.flow_control.repository.partner.CompanyPartnerRepository;
import br.com.midnightsyslabs.flow_control.repository.partner.PersonalPartnerRepository;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.geometry.Side;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ContextMenu;

@Controller
public class ClientEditFormController {

    private ClientView clientDTO;

    private final CompanyPartnerRepository companyPartnerRepository;

    private final PersonalPartnerRepository personalPartnerRepository;

    private final CityRepository cityRepository;

    private final ClientService clientService;

    private boolean loadingData = false;

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
    private TextField partnerCategoryText;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField cityField;

    public ClientEditFormController(
            CompanyPartnerRepository companyPartnerRepository,
            PersonalPartnerRepository personalPartnerRepository,
            ClientService clientService,
            CityRepository cityRepository) {
        this.companyPartnerRepository = companyPartnerRepository;
        this.personalPartnerRepository = personalPartnerRepository;
        this.clientService = clientService;
        this.cityRepository = cityRepository;
    }

    @FXML
    public void initialize() {

        citySuggestions = new ContextMenu();
        var cities = cityRepository.findAll();

        setupCityAutocomplete(cities, citySuggestions);

        setupDocumentFieldBehavior();

        documentField.setTextFormatter(MaskUtils.cpfMask());
        phoneField.setTextFormatter(MaskUtils.phoneMask());

        emailField.setTextFormatter(EmailUtils.emailFormatter());

        loadClientData();

    }

    public void loadClientData() {

        if (clientDTO == null) {
            showLabelAlert(Alert.AlertType.ERROR, "Erro", "Dados incogruentes do cliente.");
            return;
        }

        loadingData = true;

        if (clientDTO.getCategory() == PartnerCategory.PERSONAL) {
            personalPartnerRepository.findById(clientDTO.getId()).ifPresentOrElse(client -> {

                partnerCategoryText.setText(Constants.PERSONAL);
                documentField.setTextFormatter(MaskUtils.cpfMask());

                fillFields(
                        client.getName(),
                        client.getDocument(),
                        client.getEmail(),
                        client.getPhone(),
                        client.getCity());

            }, ClientNotFoundException::new);
        } else {
            companyPartnerRepository.findById(clientDTO.getId()).ifPresentOrElse(client -> {

                partnerCategoryText.setText(Constants.COMPANY);
                documentField.setTextFormatter(MaskUtils.cnpjMask());

                fillFields(
                        client.getName(),
                        client.getDocument(),
                        client.getEmail(),
                        client.getPhone(),
                        client.getCity());

            }, ClientNotFoundException::new);
        }

        loadingData = false;
    }

    private void fillFields(String name, String doc, String email, String phone, City city) {
        nameField.setText(name);
        documentField.setText(doc);
        emailField.setText(email);
        phoneField.setText(phone);
        if (city != null) {
            this.selectedCity = city;
            cityField.setText(city.getName());
        }
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

                if (cityField.getScene() == null) {
                    return; // ainda não está pronto
                }

                if (!citySuggestions.isShowing()) {
                    citySuggestions.show(cityField, Side.BOTTOM, 0, 0);
                }
            }

        });
    }

    private void setupDocumentFieldBehavior() {

        partnerCategoryText.textProperty().addListener((obs, oldValue, newValue) -> {

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

            // só limpa se NÃO estiver carregando dados
            if (!loadingData) {
                documentField.clear();
            }
        });
    }

    public void editClientForm(ClientView clientDTO) {
        this.clientDTO = clientDTO;
    }

    @FXML
    public void onEdit() {

        try {

            if (nameField.getText().isEmpty() || selectedCity == null) {
                showLabelAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                        "Por favor, preencha o nome, selecione uma cidade e a categoria de cliente.");
                return;
            }

            String document = documentField.getText() == null ? null : documentField.getText().replaceAll("\\D", "");
            String phone = phoneField.getText() == null ? null : phoneField.getText().replaceAll("\\D", "");

            if (clientDTO != null) {

                // showLabelAlert(Alert.AlertType.WARNING, "CIDADE", selectedCity.getName());

                if (clientDTO.getCategory() == PartnerCategory.PERSONAL) {
                    personalPartnerRepository.findById(clientDTO.getId()).ifPresentOrElse(client -> {

                        clientService.updatePersonalClient(
                                client,
                                nameField.getText(),
                                document,
                                phone,
                                emailField.getText(),
                                selectedCity);
                    }, ClientNotFoundException::new);
                } else {
                    companyPartnerRepository.findById(clientDTO.getId()).ifPresentOrElse(client -> {
                        clientService.updateCompanyClient(
                                client,
                                nameField.getText(),
                                document,
                                phone,
                                emailField.getText(),
                                selectedCity);
                    }, ClientNotFoundException::new);
                }
            }

        } catch (IllegalEmailArgumentException e) {
            showLabelAlert(Alert.AlertType.WARNING, "Erro de email", e.getMessage());
            return;
        }

        catch (IllegalArgumentException e) {
            showLabelAlert(Alert.AlertType.WARNING, "Dados Inválidos", e.getMessage());
            return;
        }

        catch (DataIntegrityViolationException e) {
            // showLabelAlert(Alert.AlertType.ERROR, "Erro de Integridade de Dados",
            // "O CPF, CNPJ, Telefone ou E-mail já existe no banco de dados!");
            showLabelAlert(Alert.AlertType.ERROR, "DataIntegrityViolation",
                    e.getMessage());
            return;
        }

        catch (ClientNotFoundException e) {
            showLabelAlert(Alert.AlertType.ERROR, "Cliente não encontrado",
                    e.getMessage());
            return;
        }

        catch (Exception e) {
            showLabelAlert(Alert.AlertType.ERROR, "Erro ao atualizar cliente",
                    "Ocorreu um erro ao tentar cadastrar o cliente: " + e.getMessage());
            System.err.println(e.getMessage());
            return;
        }

        close();
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showLabelAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Remove o cabeçalho extra para ficar mais limpo
        alert.setContentText(message);
        alert.showAndWait();
    }
}
