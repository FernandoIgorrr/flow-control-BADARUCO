package br.com.midnightsyslabs.flow_control.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import br.com.midnightsyslabs.flow_control.domain.entity.partner.Partner;
import br.com.midnightsyslabs.flow_control.domain.entity.product.MeasurementUnit;
import br.com.midnightsyslabs.flow_control.domain.entity.product.Product;
import br.com.midnightsyslabs.flow_control.domain.entity.production.Production;
import br.com.midnightsyslabs.flow_control.domain.entity.purchase.Purchase;
import br.com.midnightsyslabs.flow_control.view.ProductionView;
import br.com.midnightsyslabs.flow_control.repository.production.ProductionRepository;
import br.com.midnightsyslabs.flow_control.repository.view.ProductionViewRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;

@Service
public class ProductionService {
    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private ProductionRepository productionRepository;

    @Autowired
    private ProductionViewRepository productionViewRepository;
    /*
     * @Autowired
     * private ProductionRawMaterialPurchaseViewRepository prmpvRepository;
     */

    @Transactional
    public void saveProdction(
            // List<PurchaseRow> purchasesRow,
            String qunatityUsed,
            String grossProductQuantity,
            MeasurementUnit grossMeasurementUnit,
            Product product,
            String quantityProduced,
            LocalDate date,
            LocalDate rawMaterialPurchaseDate) {

        String quantityProducedPattern = UtilsService.solveComma(quantityProduced);
        String quantityUsedPattern = UtilsService.solveComma(qunatityUsed);

        var production = new Production();
        /*
         * List<ProductionRawMaterialPurchase> productionRawMaterialsPurchase = new
         * ArrayList<>();
         */
        production.setDate(date);
        production.setProduct(product);
        production.setGqpMeasurementUnit(grossMeasurementUnit);
        production.setRawMaterialPurchaseDate(rawMaterialPurchaseDate);

        try {
            production.setQuantityUsed(new BigDecimal(quantityUsedPattern));
            production.setGrossQuantityProduced(new BigDecimal(grossProductQuantity));
            production.setQuantityProduced(new BigDecimal(quantityProducedPattern));

            /*
             * for (var row : purchasesRow) {
             * 
             * ProductionRawMaterialPurchase productionRawMaterialPurchase = new
             * ProductionRawMaterialPurchase();
             * productionRawMaterialPurchase.setId(new ProductionRawMaterialsPurchaseId());
             * productionRawMaterialPurchase.setProduction(production);
             * productionRawMaterialPurchase
             * .setQuantityUsed(new
             * BigDecimal(UtilsService.solveComma(row.purchaseQuantityUsedField.getText())))
             * ;
             * 
             * purchaseService.getById(row.purchaseView.getId()).ifPresentOrElse(purchase ->
             * {
             * productionRawMaterialPurchase.setPurchase(purchase);
             * }, PurchaseNotFoundException::new);
             * 
             * productionRawMaterialsPurchase.add(productionRawMaterialPurchase);
             * }
             */

        } catch (Exception e) {
            throw e;
        }
        // production.setProductionRawMaterialsPurchase(productionRawMaterialsPurchase);
        productionRepository.save(production);
    }

    @Transactional
    public void updateProduction(
            Production production,
            String quantityUsed,
            LocalDate rawMaterialPurchaseDate,
            String quantityGrossProduced,
            MeasurementUnit gqpMeasurementUnit,
            Product product,
            String quantityProduced,
            LocalDate date
        ) {

        var quantityUsed_ = UtilsService.solveComma(quantityUsed);
        var quantityGrossProduced_ = UtilsService.solveComma(quantityGrossProduced);
        var quantityProduced_ = UtilsService.solveComma(quantityProduced);
        

                production.setRawMaterialPurchaseDate(rawMaterialPurchaseDate);
                production.setGqpMeasurementUnit(gqpMeasurementUnit);
                production.setProduct(product);
                production.setDate(date);

        try {
            production.setQuantityUsed(new BigDecimal(quantityUsed_));
            production.setGrossQuantityProduced(new BigDecimal(quantityGrossProduced_));
            production.setQuantityProduced(new BigDecimal(quantityProduced_));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Tem algo errado com formato da quantidade! em [updateProduction]");
        }

       

       productionRepository.save(production);
    }

    public List<ProductionView> getProductionsView() {
        /*
         * var productionsView = productionViewRepository.findAll();
         * 
         * var productionsDTO =
         * productionsView.stream().map(ProductionDTO::new).toList();
         * 
         * productionsDTO.forEach(pDTO -> {
         * 
         * var prmpvs = prmpvRepository.findAllByProductionId(pDTO.getId());
         * pDTO.setRawMaterialsPurchaseView(prmpvs);
         * });
         */

        return productionViewRepository.findAll().stream().sorted(Comparator.comparing(ProductionView::getDate).reversed()).toList();
    }

    public Optional<Production> findById(Integer id) {
        return productionRepository.findById(id);
    }

    @Transactional
    public void deleteProduction(Production production) {
        production.setDeletedAt(OffsetDateTime.now());
        productionRepository.save(production);
    }

    public BigDecimal productionTotalExpense(ProductionView pView) {
        return pView.getAvgRawMaterialUnitPrice().multiply(pView.getQuantityUsed());
    }

    public BigDecimal productionGrossIncome(ProductionView pView) {
        return pView.getProductCurrentPrice().multiply(pView.getQuantityProduced());
    }

    public BigDecimal productionNetIncome(ProductionView pView) {
        return productionGrossIncome(pView).subtract(productionTotalExpense(pView));
    }

    public BigDecimal productionExpensePerProductUnit(ProductionView pView) {
        return productionTotalExpense(pView).divide(pView.getQuantityProduced(), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal productionNetIncomePertUnit(ProductionView pView) {
        return pView.getProductCurrentPrice().subtract(productionExpensePerProductUnit(pView));
    }

}
