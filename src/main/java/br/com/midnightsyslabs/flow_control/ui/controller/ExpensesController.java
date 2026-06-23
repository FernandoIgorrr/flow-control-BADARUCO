package br.com.midnightsyslabs.flow_control.ui.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.config.TimeIntervalEnum;
import br.com.midnightsyslabs.flow_control.domain.entity.employee.Employee;
import br.com.midnightsyslabs.flow_control.domain.entity.employee.EmployeePayment;
import br.com.midnightsyslabs.flow_control.domain.entity.expense.Expense;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.Spent;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.SpentCategory;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.VehicleSpent;
import br.com.midnightsyslabs.flow_control.domain.entity.vehicle.Vehicle;
import br.com.midnightsyslabs.flow_control.repository.spent.SpentCategoryRepository;
import br.com.midnightsyslabs.flow_control.service.DateService;
import br.com.midnightsyslabs.flow_control.service.EmojiService;
import br.com.midnightsyslabs.flow_control.service.EmployeeService;
import br.com.midnightsyslabs.flow_control.service.ExpenseService;
import br.com.midnightsyslabs.flow_control.service.PurchaseService;
import br.com.midnightsyslabs.flow_control.service.SpentService;
import br.com.midnightsyslabs.flow_control.service.UtilsService;
import br.com.midnightsyslabs.flow_control.service.VehicleService;
import br.com.midnightsyslabs.flow_control.service.VehicleSpentService;
import br.com.midnightsyslabs.flow_control.ui.cards.EmployeePaymentCard;
import br.com.midnightsyslabs.flow_control.ui.cards.PurchaseCard;
import br.com.midnightsyslabs.flow_control.ui.cards.SpentCard;
import br.com.midnightsyslabs.flow_control.ui.cards.VehicleSpentCard;
import br.com.midnightsyslabs.flow_control.ui.controller.form.SpentFormController;
import br.com.midnightsyslabs.flow_control.view.PurchaseView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;

@Controller
public class ExpensesController {

    @Autowired
    private SpentService spentService;

    @Autowired
    private EmojiService emojiService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleSpentService vehicleSpentService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private SpentCategoryRepository spentCategoryRepository;

    @Autowired
    private ApplicationContext context;

    private List<Expense> allExpenses;

    private List<Expense> filteredExpenses;

    @FXML
    private ComboBox<TimeIntervalEnum> timeIntervalEnumComboBoxFilter;

    @FXML
    private ComboBox<SpentCategory> spentCategoryComboBoxFilter;

    @FXML
    private ComboBox<Employee> employeeComboBoxFilter;

    @FXML
    private ImageView imgType;

    @FXML
    private Button btnAddSpent;

    @FXML
    private VBox cardsPane;

    @FXML
    private Label lblTotalExpenses;

    @FXML
    private Label lblTotalExpensesSpend;

    @FXML
    private HBox employeeFilterContainer;

    @FXML
    private HBox vehicleFilterContainer;

    @FXML
    private ComboBox<Vehicle> vehicleComboBoxFilter;

    @FXML
    public void initialize() {

        loadExpenses();

        // configureTimeIntervalEnumComboBoxFilter();
        configureSpentCategoryComboBoxFilter();
        configureEmployeeComboBoxFilter();
        configureVehicleComboBoxFilter();
        configureVisibilityControl(); // 🔹 Nova chamada aqui

        imgType.setImage(new Image(
                getClass().getResourceAsStream("/images/game-icons--basket.png")));
        imgType.getStyleClass().add("blue-icon");
    }

    private void configureVehicleComboBoxFilter() {
        // Carrega a lista de veículos do seu service/banco
        vehicleComboBoxFilter.getItems().setAll(vehicleService.getVehicles());

        // Exemplo simulado de mock ou add do "Todos":
        vehicleComboBoxFilter.getItems().add(new Vehicle()); // Instância vazia para representar o "Todos"

        vehicleComboBoxFilter.setConverter(new StringConverter<Vehicle>() {
            @Override
            public String toString(Vehicle v) {
                // Ajuste o método de pegar o nome/placa do veículo (ex: v.getPlate() ou
                // v.getName())
                return (v == null || v.getNumberPlate() == null) ? "Todos" : v.getNumberPlate();
            }

            @Override
            public Vehicle fromString(String s) {
                return null;
            }
        });

        vehicleComboBoxFilter.getSelectionModel().selectLast(); // Seleciona o "Todos"

        vehicleComboBoxFilter.valueProperty().addListener((obs, old, newVal) -> {
            applyFilters();
        });
    }

    private void configureVisibilityControl() {
        // Vincula as propriedades managed ao visible para ambos sumirem sem deixar
        // buraco
        employeeFilterContainer.managedProperty().bind(employeeFilterContainer.visibleProperty());
        vehicleFilterContainer.managedProperty().bind(vehicleFilterContainer.visibleProperty());

        // Escuta a mudança da categoria de gasto
        spentCategoryComboBoxFilter.valueProperty().addListener((obs, oldCategory, newCategory) -> {
            if (newCategory != null && newCategory.getId() != null) {
                long id = newCategory.getId();

                // Lógica do Funcionário (ID 2)
                if (id == 2) {
                    employeeFilterContainer.setVisible(true);
                } else {
                    employeeComboBoxFilter.getSelectionModel().selectLast();
                    employeeFilterContainer.setVisible(false);
                }

                // 🔥 VEÍCULOS agora inclui também o grupo (-1)
                if (id == 10 || id == 11 || id == -1) {
                    vehicleFilterContainer.setVisible(true);
                } else {
                    vehicleComboBoxFilter.getSelectionModel().selectLast();
                    vehicleFilterContainer.setVisible(false);
                }
            } else {
                // Se for nulo ou "Todos", esconde ambos
                employeeComboBoxFilter.getSelectionModel().selectLast();
                employeeFilterContainer.setVisible(false);

                vehicleComboBoxFilter.getSelectionModel().selectLast();
                vehicleFilterContainer.setVisible(false);
            }
        });

        // Estado inicial
        SpentCategory currentCategory = spentCategoryComboBoxFilter.getValue();
        long currentId = (currentCategory != null && currentCategory.getId() != null) ? currentCategory.getId() : -1;

        employeeFilterContainer.setVisible(currentId == 2);
        vehicleFilterContainer.setVisible(currentId == 10 || currentId == 11);
    }

    public void configureTimeIntervalEnumComboBoxFilter() {
        this.timeIntervalEnumComboBoxFilter.getItems().setAll(TimeIntervalEnum.values());

        this.timeIntervalEnumComboBoxFilter.setConverter(new StringConverter<>() {
            @Override
            public String toString(TimeIntervalEnum interval) {
                return interval == null ? "" : interval.getLabel();
            }

            @Override
            public TimeIntervalEnum fromString(String string) {
                return null;
            }
        });

        this.timeIntervalEnumComboBoxFilter.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null)
                return;
            applyFilters();
        });

        this.timeIntervalEnumComboBoxFilter.getSelectionModel().selectFirst();
    }

    private void configureSpentCategoryComboBoxFilter() {
        spentCategoryComboBoxFilter.getItems().setAll(
                spentCategoryRepository.findAll());

        SpentCategory vehicleGroup = new SpentCategory();
        vehicleGroup.setId((short) -1L); // ID virtual
        vehicleGroup.setName("Veículos");

        spentCategoryComboBoxFilter.getItems().add(vehicleGroup);

        spentCategoryComboBoxFilter.getItems().add(new SpentCategory());
        spentCategoryComboBoxFilter.setConverter(new StringConverter<SpentCategory>() {
            @Override
            public String toString(SpentCategory sc) {
                return (sc == null || sc.getName() == null) ? "Todos" : sc.getName();
            }

            @Override
            public SpentCategory fromString(String s) {
                return null;
            }
        });

        spentCategoryComboBoxFilter.valueProperty().addListener((obs, old, newVal) -> {

            applyFilters();
        });
        spentCategoryComboBoxFilter.getSelectionModel().selectLast();
    }

    private void configureEmployeeComboBoxFilter() {
        employeeComboBoxFilter.getItems().setAll(
                employeeService.getEmployees());

        employeeComboBoxFilter.getItems().add(new Employee());
        employeeComboBoxFilter.setConverter(new StringConverter<Employee>() {
            @Override
            public String toString(Employee e) {
                return (e == null || e.getName() == null) ? "Todos" : e.getName();
            }

            @Override
            public Employee fromString(String s) {
                return null;
            }
        });

        employeeComboBoxFilter.getSelectionModel().selectLast();

        employeeComboBoxFilter.valueProperty().addListener((obs, old, newVal) -> {

            applyFilters();
        });
    }

    @FXML
    public void onAddSpent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form/spent-form.fxml"));

            loader.setControllerFactory(context::getBean);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.3;
            double height = screenBounds.getHeight() * 0.5;

            Stage dialog = new Stage();
            dialog.setTitle("Cadastrar Despesa");
            dialog.setScene(new Scene(loader.load(), width, height));

            SpentFormController controller = loader.getController();
            controller.setOnDataChanged(this::loadExpenses);

            Stage mainStage = (Stage) btnAddSpent.getScene().getWindow();

            dialog.initOwner(mainStage);
            dialog.initModality(Modality.WINDOW_MODAL);

            dialog.setResizable(false);
            // stage.showAndWait();

            ColorAdjust darken = new ColorAdjust();
            darken.setBrightness(-0.8);
            mainStage.getScene().getRoot().setEffect(darken);

            dialog.setOnHidden(e -> mainStage.getScene().getRoot().setEffect(null));

            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderCards(List<Expense> expenses) {

        cardsPane.getChildren().clear();

        for (var expense : expenses) {
            try {
                if (expense.getSpentCategory().getId() == 1) {

                    cardsPane.getChildren()
                            .add(new PurchaseCard((PurchaseView) expense, this::loadExpenses, purchaseService,
                                    context));

                } else if (expense.getSpentCategory().getId() == 2) {

                    cardsPane.getChildren().add(new EmployeePaymentCard((EmployeePayment) expense, this::loadExpenses,
                            employeeService, emojiService));
                } else if (expense.getSpentCategory().getId() == 10 || expense.getSpentCategory().getId() == 11) {

                    cardsPane.getChildren().add(new VehicleSpentCard((VehicleSpent) expense, this::loadExpenses,
                            vehicleSpentService, context));

                } else {

                    cardsPane.getChildren().add(
                            new SpentCard((Spent) expense, this::loadExpenses, spentService, emojiService, context));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void applyFilters() {

        filteredExpenses = allExpenses.stream()

                // 🔹 filtro por categoria de gasto
                .filter(expense -> {

                    SpentCategory selected = spentCategoryComboBoxFilter.getValue();

                    if (selected == null || selected.getName() == null) {
                        return true;
                    }

                    Short selectedId = selected.getId();

                    // 🔥 NOVA OPÇÃO: VEÍCULOS (10 + 11)
                    if (selectedId != null && selectedId == -1L) {
                        return expense.getSpentCategory() != null &&
                                (expense.getSpentCategory().getId() == 10
                                        || expense.getSpentCategory().getId() == 11);
                    }

                    // 🔥 FILTRO NORMAL
                    return expense.getSpentCategory() != null &&
                            expense.getSpentCategory().getId().equals(selectedId);
                })

                // 🔹 filtro por funcionário
                .filter(expense -> employeeComboBoxFilter.getValue() == null
                        || employeeComboBoxFilter.getValue().getName() == null
                        || (expense instanceof EmployeePayment ep
                                && ep.getEmployee().getId()
                                        .equals(employeeComboBoxFilter.getValue().getId())))

                // 🔹 NOVO: filtro por veículo (só aplica se for uma instância de VehicleSpent)
                .filter(expense -> vehicleComboBoxFilter.getValue() == null
                        || vehicleComboBoxFilter.getValue().getNumberPlate() == null // Ajuste para o atributo que
                                                                                     // valida o
                        // "Todos"
                        || (expense instanceof VehicleSpent vs
                                && vs.getVehicle().getNumberPlate() // Ajuste conforme os getters da sua classe
                                                                    // VehicleSpent
                                        .equals(vehicleComboBoxFilter.getValue().getNumberPlate())))

                // 🔹 filtro por intervalo de datas
                .filter(expense -> {
                    if (timeIntervalEnumComboBoxFilter.getValue() == TimeIntervalEnum.ALL_TIME) {
                        return true;
                    }

                    LocalDate from = DateService.timeIntervalEnumToDateFrom(timeIntervalEnumComboBoxFilter.getValue());
                    LocalDate to = DateService.timeIntervalEnumToDateTo(timeIntervalEnumComboBoxFilter.getValue());

                    return !expense.getDate().isBefore(from) && !expense.getDate().isAfter(to);
                })

                .toList();

        renderCards(filteredExpenses);
        renderRecentExpenseAmountCard();
    }

    private void renderRecentExpenseAmountCard() {
        lblTotalExpensesSpend.setText(UtilsService.formatPrice(
                filteredExpenses.stream().map(Expense::getExpense).reduce(BigDecimal.ZERO, BigDecimal::add)));
        lblTotalExpenses.setText("" + filteredExpenses.size());
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private void loadExpenses() {
        allExpenses = expenseService.getAllExpenses();

        filteredExpenses = allExpenses;

        configureTimeIntervalEnumComboBoxFilter();

        renderCards(filteredExpenses);
        renderRecentExpenseAmountCard();
    }

}