package br.com.midnightsyslabs.flow_control.ui.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.midnightsyslabs.flow_control.domain.entity.expense.Expense;
import br.com.midnightsyslabs.flow_control.domain.entity.revenue.Revenue;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.VehicleSpent;
import br.com.midnightsyslabs.flow_control.dto.SaleDTO;
import br.com.midnightsyslabs.flow_control.service.ExpenseService;
import br.com.midnightsyslabs.flow_control.service.PurchaseService;
import br.com.midnightsyslabs.flow_control.service.SaleService;
import br.com.midnightsyslabs.flow_control.service.VehicleSpentService;
import br.com.midnightsyslabs.flow_control.view.PurchaseView;
import br.com.midnightsyslabs.flow_control.view.SaleProductView;
import br.com.midnightsyslabs.flow_control.view.VehicleSpentView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart; // Alterado para AreaChart
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;

@Controller
public class StatementFinanceController {

        @Autowired
        private SaleService saleService;

        @Autowired
        private ExpenseService expenseService;

        @Autowired
        private PurchaseService purchaseService;

        @Autowired
        private VehicleSpentService vehicleSpentService;

        @FXML
        private DatePicker dpInicio;
        @FXML
        private DatePicker dpFim;

        @FXML
        private PieChart categoryPieChart;

        @FXML
        private AreaChart<String, Number> financialLineChart;

        @FXML
        private BarChart<String, Number> clientBarChart;

        @FXML
        private BarChart<String, Number> purchaseBarChart;

        @FXML
        private BarChart<String, Number> productBarChart;

        @FXML
        private BarChart<String, Number> vehicleBarChart;

        @FXML
        private Label lblTotalReceita;
        @FXML
        private Label lblTotalDespesa;
        @FXML
        private Label lblSaldo;

        @FXML
        private TabPane tabPane;

        private static final Locale ptBr = Locale.forLanguageTag("pt-BR");

        private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        private static final DateTimeFormatter BR_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00",
                        new DecimalFormatSymbols(ptBr));

        @FXML
        public void initialize() {
                // tabPane.tabMinWidthProperty().bind(tabPane.widthProperty().divide(5).subtract(2));

                tabPane.tabMinWidthProperty().bind(
                                tabPane.widthProperty()
                                                .divide(tabPane.getTabs().size())
                                                .subtract(2));

                tabPane.tabMaxWidthProperty().bind(
                                tabPane.tabMinWidthProperty());

                // Define o mês atual por padrão
                dpInicio.setValue(LocalDate.now().withDayOfYear(1));
                dpFim.setValue(LocalDate.now());

                dpInicio.setEditable(false);
                dpFim.setEditable(false);

                // Rotaciona os nomes dos produtos no eixo X para evitar sobreposição
                CategoryAxis xAxisProduct = (CategoryAxis) productBarChart.getXAxis();
                CategoryAxis xAxisClient = (CategoryAxis) clientBarChart.getXAxis();
                CategoryAxis xAxisPurchase = (CategoryAxis) purchaseBarChart.getXAxis();
                CategoryAxis xAxisVehicle = (CategoryAxis) vehicleBarChart.getXAxis();

                xAxisClient.setTickLabelRotation(45);
                xAxisProduct.setTickLabelRotation(45);
                xAxisPurchase.setTickLabelRotation(45);
                xAxisVehicle.setTickLabelRotation(45);

                configureCategoryAxis(productBarChart);
                configureCategoryAxis(clientBarChart);
                configureCategoryAxis(purchaseBarChart);
                configureCategoryAxis(vehicleBarChart);

                // Chama o filtro inicial para carregar os dados
                handleFiltrar();
        }

        private void applyCurrencyTooltip(XYChart.Data<String, Number> data, String labelPrefix, String quantity) {
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                                BigDecimal value = new BigDecimal(data.getYValue().toString());

                                // Monta o texto base com o valor financeiro
                                String tooltipText = data.getXValue() + "\n" + labelPrefix + ": "
                                                + CURRENCY_FORMAT.format(value);

                                // Se houver quantidade, adiciona na linha de baixo
                                if (quantity != null) {
                                        tooltipText += "\n" + quantity;

                                }

                                Tooltip tooltip = new Tooltip(tooltipText);

                                tooltip.setStyle("""
                                                    -fx-font-size: 16px;
                                                    -fx-font-weight: bold;
                                                    -fx-padding: 12;
                                                """);

                                // Configura para aparecer instantaneamente e não sumir por tempo
                                tooltip.setShowDelay(javafx.util.Duration.ZERO);
                                tooltip.setHideDelay(javafx.util.Duration.INDEFINITE);

                                Tooltip.install(newNode, tooltip);

                                // --- GARANTE QUE SOME IMEDIATAMENTE AO SAIR ---
                                // Quando o mouse sair da barra, força o tooltip a se esconder
                                newNode.setOnMouseExited(event -> {
                                        if (tooltip.isShowing()) {
                                                tooltip.hide();
                                        }
                                });
                                // ----------------------------------------------
                        }
                });
        }

        private void configureCategoryAxis(BarChart<String, Number> chart) {
                CategoryAxis axis = (CategoryAxis) chart.getXAxis();
                axis.setAutoRanging(true);
                axis.setTickLabelRotation(45); // ou 60 se nomes grandes
                axis.setTickLabelGap(5);
                axis.setAnimated(false);
                // axis.getStyleClass().add("axis-label");
                axis.setTickLabelFont(
                                javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 14));
        }

        @FXML
        void handleFiltrar() {
                LocalDate start = dpInicio.getValue();
                LocalDate end = dpFim.getValue();

                if (start != null && end != null) {
                        List<SaleDTO> revs = saleService.searchBetween(start, end);
                        List<Expense> exps = expenseService.searchBetween(start, end);
                        List<PurchaseView> purchases = purchaseService.getPurchasesFromDate(
                                        purchaseService.getPurchasesView(),
                                        start, end);
                        List<VehicleSpentView> vehicleSpents = vehicleSpentService.searchBetweenView(start, end);

                        updatePieChart(exps);
                        updateClientChart(revs);
                        updatePurchaseChart(purchases);
                        updateLineChart(revs, exps);
                        updateVehicleChart(vehicleSpents);
                        updateProductChart(revs);
                        atualizarLabelsTotais(revs, exps);

                }
        }

        public void updatePieChart(List<Expense> expenses) {
                Map<String, Double> dataMap = expenses.stream()
                                .collect(Collectors.groupingBy(
                                                e -> e.getSpentCategory() != null ? e.getSpentCategory().getName()
                                                                : "Outros",
                                                Collectors.summingDouble(e -> e.getExpense().doubleValue())));

                ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

                dataMap.forEach((name, value) -> {
                        if (value > 0) {
                                String label = name + " (" + CURRENCY_FORMAT.format(value) + ")";
                                pieData.add(new PieChart.Data(label, value));
                        }
                });

                categoryPieChart.setData(pieData);
        }

        public void updateClientChart(List<SaleDTO> sales) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Faturamento por Cliente");

                // Classe utilitária local para guardar o faturamento e o detalhamento de pesos
                // por produto
                record ClientStats(BigDecimal faturamento, Map<String, BigDecimal> pesosPorProduto) {
                }

                Map<String, ClientStats> clientMap = sales.stream()
                                .collect(Collectors.groupingBy(
                                                s -> s.getClientName() != null ? s.getClientName()
                                                                : "Cliente não identificado",
                                                Collectors.collectingAndThen(
                                                                Collectors.toList(),
                                                                list -> {
                                                                        // 1. Calcula o faturamento total do cliente
                                                                        BigDecimal totalFaturamento = list.stream()
                                                                                        .map(SaleDTO::getRevenue)
                                                                                        .reduce(BigDecimal.ZERO,
                                                                                                        BigDecimal::add);

                                                                        // 2. Agrupa e soma o peso vendido de cada
                                                                        // produto para este cliente
                                                                        Map<String, BigDecimal> pesosProdutos = list
                                                                                        .stream()
                                                                                        .filter(s -> s.getSaleProductsView() != null)
                                                                                        .flatMap(s -> s.getSaleProductsView()
                                                                                                        .stream())
                                                                                        .collect(Collectors.groupingBy(
                                                                                                        SaleProductView::getProductName,
                                                                                                        Collectors.reducing(
                                                                                                                        BigDecimal.ZERO,
                                                                                                                        p -> p.getProductWeight() != null
                                                                                                                                        ? p.getProductWeight()
                                                                                                                                                        .multiply(p.getProductQuantitySold())
                                                                                                                                        : BigDecimal.ZERO,
                                                                                                                        BigDecimal::add)));

                                                                        return new ClientStats(totalFaturamento,
                                                                                        pesosProdutos);
                                                                })));

                clientMap.entrySet().stream()
                                .sorted((e1, e2) -> e2.getValue().faturamento().compareTo(e1.getValue().faturamento())) // Ordena
                                                                                                                        // por
                                                                                                                        // faturamento
                                .limit(10)
                                .forEach(entry -> {
                                        XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(),
                                                        entry.getValue().faturamento());

                                        // 3. Monta a String do Tooltip com a quebra de linha contendo os produtos e
                                        // seus respectivos pesos
                                        StringBuilder stringBuilder = new StringBuilder("\n--- Produtos Vendidos ---");

                                        entry.getValue().pesosPorProduto().forEach((produto, pesoTotal) -> {
                                                if (pesoTotal.compareTo(BigDecimal.ZERO) > 0) {
                                                        stringBuilder.append("\n• ")
                                                                        .append(produto)
                                                                        .append(": ")
                                                                        .append(DECIMAL_FORMAT.format(pesoTotal))
                                                                        .append(" Kg");
                                                }
                                        });

                                        // Se o cliente não comprou nenhum produto com peso cadastrado
                                        String tooltipComplemento = stringBuilder.toString();

                                        // Passa o faturamento e a lista de pesos montada para o método utilitário de
                                        // Tooltip
                                        applyCurrencyTooltip(data, "Total faturado", tooltipComplemento);

                                        series.getData().add(data);
                                });

                clientBarChart.getData().setAll(series);
        }

        public void updatePurchaseChart(List<PurchaseView> purchases) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Gastos por Fornecedor");

                // 1. Agrupamento calculando Gasto Total e Litros Totais ao mesmo tempo
                Map<String, PartnerPurchaseTotals> purchaseMap = purchases.stream()
                                .collect(Collectors.groupingBy(
                                                p -> p.getPartnerName() != null ? p.getPartnerName()
                                                                : "Fornecedor não identificado",
                                                Collectors.reducing(
                                                                new PartnerPurchaseTotals(BigDecimal.ZERO,
                                                                                BigDecimal.ZERO),
                                                                p -> {
                                                                        BigDecimal expense = p.getExpense() != null
                                                                                        ? p.getExpense()
                                                                                        : BigDecimal.ZERO;
                                                                        BigDecimal quantity = p.getQuantity() != null
                                                                                        ? p.getQuantity()
                                                                                        : BigDecimal.ZERO;
                                                                        return new PartnerPurchaseTotals(expense,
                                                                                        quantity);
                                                                },
                                                                PartnerPurchaseTotals::merge)));

                // 2. Ordenação pelo total gasto e montagem do gráfico
                purchaseMap.entrySet().stream()
                                .sorted(Map.Entry.<String, PartnerPurchaseTotals>comparingByValue(
                                                Comparator.comparing(PartnerPurchaseTotals::totalExpense)).reversed())
                                .limit(10)
                                .forEach(entry -> {
                                        // Passa o valor financeiro para a barra do gráfico
                                        XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(),
                                                        entry.getValue().totalExpense());

                                        // Formata os litros para exibir no Tooltip ao passar o mouse
                                        // String tooltipComplemento = String.format("Volume: %.2f L",
                                        // entry.getValue().totalQuantity());

                                        String tooltipComplemento = "Volume: "
                                                        + DECIMAL_FORMAT.format(entry.getValue().totalQuantity())
                                                        + " L";

                                        applyCurrencyTooltip(data, "Total gasto", tooltipComplemento);

                                        series.getData().add(data);
                                });

                purchaseBarChart.getData().setAll(series);
        }

        public void updateLineChart(List<SaleDTO> revenues, List<Expense> expenses) {
                XYChart.Series<String, Number> revSeries = new XYChart.Series<>();
                revSeries.setName("Receitas");

                XYChart.Series<String, Number> expSeries = new XYChart.Series<>();
                expSeries.setName("Despesas");

                // TreeMap garante que as datas fiquem em ordem crescente no gráfico
                Map<LocalDate, Double> revByDate = revenues.stream()
                                .collect(Collectors.groupingBy(Revenue::getDate, TreeMap::new,
                                                Collectors.summingDouble(r -> r.getRevenue().doubleValue())));

                Map<LocalDate, Double> expByDate = expenses.stream()
                                .collect(Collectors.groupingBy(Expense::getDate, TreeMap::new,
                                                Collectors.summingDouble(e -> e.getExpense().doubleValue())));

                // Adiciona os dados às séries
                revByDate.forEach((date, val) -> revSeries.getData().add(
                                new XYChart.Data<>(date.format(BR_DATE_FORMAT), val)));

                expByDate.forEach((date, val) -> expSeries.getData().add(
                                new XYChart.Data<>(date.format(BR_DATE_FORMAT), val)));

                financialLineChart.getData().setAll(revSeries, expSeries);

        }

        private void atualizarLabelsTotais(List<SaleDTO> revs, List<Expense> exps) {
                BigDecimal totalRev = revs.stream()
                                .map(Revenue::getRevenue)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalExp = exps.stream()
                                .map(Expense::getExpense)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal saldo = totalRev.subtract(totalExp);

                lblTotalReceita.setText(CURRENCY_FORMAT.format(totalRev));
                lblTotalDespesa.setText(CURRENCY_FORMAT.format(totalExp));
                lblSaldo.setText(CURRENCY_FORMAT.format(saldo));

                // Estilização dinâmica do saldo
                if (saldo.compareTo(BigDecimal.ZERO) < 0) {
                        lblSaldo.getStyleClass().add("total-price-info-red");
                } else {
                        lblSaldo.getStyleClass().add("total-price-info");
                }
        }

        public void updateProductChart(List<SaleDTO> sales) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Faturamento por Produto");

                Map<String, ProductTotals> productMap = sales.stream()
                                .filter(s -> s.getSaleProductsView() != null)
                                .flatMap(sale -> sale.getSaleProductsView().stream())
                                .collect(Collectors.groupingBy(
                                                SaleProductView::getProductName,
                                                Collectors.reducing(
                                                                new ProductTotals(BigDecimal.ZERO, BigDecimal.ZERO),
                                                                p -> {
                                                                        // Calcula o faturamento deste item
                                                                        BigDecimal revenue = p
                                                                                        .getProductPriceOnSaleDate()
                                                                                        .multiply(p.getProductQuantitySold());

                                                                        // Calcula o peso total deste item (peso
                                                                        // unitário * quantidade vendida)
                                                                        BigDecimal weight = p.getProductWeight() != null
                                                                                        ? p.getProductWeight().multiply(
                                                                                                        p.getProductQuantitySold())
                                                                                        : BigDecimal.ZERO;

                                                                        return new ProductTotals(revenue, weight);
                                                                },
                                                                ProductTotals::merge)));

                productMap.entrySet().stream()
                                // 1. Mudou aqui: Comparator agora extrai o faturamento (totalRevenue) do objeto
                                // ProductTotals
                                .sorted(Map.Entry.<String, ProductTotals>comparingByValue(
                                                Comparator.comparing(ProductTotals::totalRevenue)).reversed())
                                .limit(10)
                                .forEach(entry -> {
                                        // 2. Mudou aqui: Passamos o faturamento para o valor do gráfico
                                        XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(),
                                                        entry.getValue().totalRevenue());

                                        // Dica bônus: Você pode passar o peso acumulado para o Tooltip se quiser exibir
                                        // ao passar o mouse!
                                        String tooltipComplemento = "Peso total: "
                                                        + DECIMAL_FORMAT.format(entry.getValue().totalWeight()) + " Kg";
                                        applyCurrencyTooltip(data, "Faturamento", tooltipComplemento);

                                        series.getData().add(data);
                                });

                productBarChart.getData().setAll(series);
        }

        public void updateVehicleChart(List<VehicleSpentView> spends) {

                record VehicleChartData(
                                BigDecimal fuel,
                                BigDecimal maintenance) {
                }

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Gastos por Veículo");

                /*
                 * Map<String, BigDecimal> vehicleMap =
                 * spends.stream().collect(Collectors.groupingBy(
                 * s -> s.getVehicleNumberPlate() + " - " + s.getModel(),
                 * Collectors.reducing(BigDecimal.ZERO, VehicleSpentView::getExpense,
                 * BigDecimal::add)));
                 */

                Map<String, VehicleChartData> vehicleMap = spends.stream()
                                .collect(Collectors.groupingBy(s -> s.getVehicleNumberPlate() + " - " + s.getModel(),
                                                Collectors.collectingAndThen(
                                                                Collectors.toList(),
                                                                list -> {

                                                                        BigDecimal fuel = list.stream()
                                                                                        .filter(v -> v.getSpentCategory()
                                                                                                        .getId() == 10)
                                                                                        .map(VehicleSpentView::getExpense)
                                                                                        .reduce(BigDecimal.ZERO,
                                                                                                        BigDecimal::add);

                                                                        BigDecimal maintenance = list.stream()
                                                                                        .filter(v -> v.getSpentCategory()
                                                                                                        .getId() == 11)
                                                                                        .map(VehicleSpentView::getExpense)
                                                                                        .reduce(BigDecimal.ZERO,
                                                                                                        BigDecimal::add);

                                                                        return new VehicleChartData(fuel, maintenance);
                                                                })));

                vehicleMap.entrySet().stream()
                                .sorted((e1, e2) -> e2.getValue().fuel().add(e2.getValue().maintenance())
                                                .compareTo(
                                                                e1.getValue().fuel().add(e1.getValue().maintenance())))
                                .limit(10)
                                .forEach(entry -> {

                                        XYChart.Data<String, Number> data = new XYChart.Data<>(
                                                        entry.getKey(),
                                                        entry.getValue()
                                                                        .fuel()
                                                                        .add(entry.getValue().maintenance()));

                                        StringBuilder sb = new StringBuilder("\n--- Gastos por Categoria ---");

                                        if (entry.getValue().fuel().compareTo(BigDecimal.ZERO) > 0) {
                                                sb.append("\n⛽ Combustível: ")
                                                                .append(DECIMAL_FORMAT.format(entry.getValue().fuel()))
                                                                .append(" R$");
                                        }

                                        if (entry.getValue().maintenance().compareTo(BigDecimal.ZERO) > 0) {
                                                sb.append("\n🔧 Manutenção: ")
                                                                .append(DECIMAL_FORMAT
                                                                                .format(entry.getValue().maintenance()))
                                                                .append(" R$");
                                        }

                                        String tooltipComplemento = sb.toString();

                                        applyCurrencyTooltip(
                                                        data,
                                                        "Total gasto",
                                                        tooltipComplemento);

                                        series.getData().add(data);
                                });

                vehicleBarChart.getData().setAll(series);
        }
}
