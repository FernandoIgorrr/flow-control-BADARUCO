package br.com.midnightsyslabs.flow_control.ui.controller.form.edit;

import java.time.LocalDate;
import java.util.function.UnaryOperator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.domain.entity.spent.Spent;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.SpentCategory;
import br.com.midnightsyslabs.flow_control.repository.spent.SpentCategoryRepository;
import br.com.midnightsyslabs.flow_control.service.EmojiService;
import br.com.midnightsyslabs.flow_control.service.SpentService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import br.com.midnightsyslabs.flow_control.ui.utils.UiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

@Controller
public class SpentEditFormController {

    private final SpentCategoryRepository spentCategoryRepository;

    private final EmojiService emojiService;

    private final SpentService spentService;

    private Runnable onDataChanged;

    private Spent spent;

    @FXML
    private ComboBox<SpentCategory> spentCategoryComboBox;

    @FXML
    private TextField amountPaidField;

    @FXML
    private TextField spentDescriptionField;

    @FXML
    private DatePicker datePicker;

    public SpentEditFormController(SpentCategoryRepository spentCategoryRepository,
            EmojiService emojiService,
            SpentService spentService) {
        this.spentCategoryRepository = spentCategoryRepository;
        this.emojiService = emojiService;
        this.spentService = spentService;

    }

    @FXML
    public void initialize() {
        configureSpentCategoryComboBox();
        UiUtils.configurePriceField(amountPaidField);
        datePicker.setValue(LocalDate.now());
        datePicker.setEditable(false);

        loadSpentData();
    }

    public void configureSpentCategoryComboBox() {
        var spentCategories = spentCategoryRepository.findAll();

        spentCategories = spentCategories.stream()
                .filter(sc -> sc.getId() > 2).toList();

        spentCategoryComboBox.getItems().setAll(spentCategories);
        spentCategoryComboBox.setConverter(new StringConverter<SpentCategory>() {

            @Override
            public String toString(SpentCategory spentCategory) {
                return spentCategory == null ? ""
                        : emojiService.getEmoji(spentCategory.getId()) + " " + spentCategory.getName();
            }

            @Override
            public SpentCategory fromString(String string) {
                return null;
            }

        });

        if (!spentCategories.isEmpty()) {
            spentCategoryComboBox.getSelectionModel().selectFirst();
        }

    }

    public void loadSpentData() {

        if (spent == null) {
            UiUtils.showLabelAlert(Alert.AlertType.ERROR, "Erro", "Dados incogruentes da despesa.");
            return;
        }

        spentCategoryComboBox.getItems()
                .stream()
                .filter(spentCategory -> spentCategory.getId().equals(spent.getCategory().getId()))
                .findFirst()
                .ifPresent(spentCategoryComboBox.getSelectionModel()::select);

        fillFields(UtilsService.solveDot(spent.getAmountPaid().toString()),
                spent.getDescription(),
                spent.getDate());

    }

    public void editSpentForm(Spent spent) {
        this.spent = spent;
    }

    private void fillFields(String amountPaid, String description,
            LocalDate date) {
        amountPaidField.setText(amountPaid);
        spentDescriptionField.setText(description);
        datePicker.setValue(date);
    }

    @FXML
    public void onEdit() {

        if (amountPaidField.getText().isBlank()) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Atenção", "A quantia paga está vazia");
            return;
        }

        try {
            spentService.updateSpent(spent, amountPaidField.getText(), spentCategoryComboBox.getValue(),
                    spentDescriptionField.getText(), datePicker.getValue());

            if (onDataChanged != null) {
                onDataChanged.run();
            }
        } catch (Exception e) {
            UiUtils.showLabelAlert(Alert.AlertType.WARNING, "Atenção", "Algo deu errado!" + e.getMessage());
            return;
        }

        close();

        UiUtils.showLabelAlert(Alert.AlertType.INFORMATION, "SUCESSO",
                "Despesa cadastrada com sucesso!");
    }

    @FXML
    public void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) spentCategoryComboBox.getScene().getWindow();
        stage.close();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }
}
