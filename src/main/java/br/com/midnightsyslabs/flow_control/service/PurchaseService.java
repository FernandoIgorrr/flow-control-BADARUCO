package br.com.midnightsyslabs.flow_control.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import br.com.midnightsyslabs.flow_control.domain.entity.partner.Partner;
import br.com.midnightsyslabs.flow_control.domain.entity.product.MeasurementUnit;
import br.com.midnightsyslabs.flow_control.domain.entity.purchase.Purchase;
import br.com.midnightsyslabs.flow_control.domain.entity.raw_material.RawMaterial;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.Spent;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.SpentCategory;
import br.com.midnightsyslabs.flow_control.repository.PurchaseRepository;
import br.com.midnightsyslabs.flow_control.repository.partner.PartnerRepository;
import br.com.midnightsyslabs.flow_control.repository.view.PurchaseViewRepository;
import br.com.midnightsyslabs.flow_control.view.PurchaseView;
import jakarta.transaction.Transactional;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PurchaseViewRepository purchaseViewRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Transactional
    public void savePurchase(
            Partner partner,
            RawMaterial rawMaterial,
            String quantity,
            MeasurementUnit measurementUnit,
            String price,
            LocalDate date,
            String note) {

        var price_ = solveComma(price);
        var quantity_ = solveComma(quantity);

        var purchase = new Purchase();

        partner.setConfirmed(true);

        purchase.setPartner(partner);
        purchase.setRawMaterial(rawMaterial);
        purchase.setSpentCategory(new SpentCategory((short) 1, ""));

        try {
            purchase.setPricePerUnit(new BigDecimal(price_));
            purchase.setQuantity(new BigDecimal(quantity_));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Tem algo errado com formato do preço ou com a quantidade!");
        }

        purchase.setMeasurementUnit(measurementUnit);
        purchase.setDate(date);
        purchase.setCreatedAt(OffsetDateTime.now());
        purchase.setNote(note);

        partnerRepository.save(partner);
        purchaseRepository.save(purchase);
    }



     @Transactional
    public void updatePurchase(
            Purchase purchase,
            Partner partner,
            String quantity,
            String price,
            LocalDate date,
            String note) {

        var price_ = solveComma(price);
        var quantity_ = solveComma(quantity);

        partner.setConfirmed(true);

        purchase.setPartner(partner);

        try {
            purchase.setPricePerUnit(new BigDecimal(price_));
            purchase.setQuantity(new BigDecimal(quantity_));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Tem algo errado com formato do preço ou com a quantidade!");
        }

        purchase.setDate(date);
        purchase.setNote(note);

        partnerRepository.save(partner);
        purchaseRepository.save(purchase);
    }

    @Transactional
    public void confirmPurchase(Purchase purchase) {
        purchase.setConfirmed(true);
        purchaseRepository.save(purchase);
    }

    @Transactional
    public void deletePurchase(Purchase purchase) {
        purchase.setDeletedAt(OffsetDateTime.now());
        purchaseRepository.save(purchase);
    }

    public Optional<Purchase> findById(Integer id) {
        return purchaseRepository.findById(id);
    }

    public List<Purchase> getPurchases() {
        return purchaseRepository.findAll();
    }

    public List<PurchaseView> getPurchasesView() {
        return purchaseViewRepository.findAll();
    }
    public Page<PurchaseView> getPurchasesViewPaged(int page, int size) {
        return purchaseViewRepository.findAll(PageRequest.of(page, size));
    }

    // Mais antigo primeiro
    public List<PurchaseView> getPurchasesViewDateOrdened() {
        return getPurchasesView()
                .stream()
                .sorted(Comparator.comparing(PurchaseView::getDate))
                .toList();

    }

    // Mais recente primeiro
    public List<PurchaseView> getPurchasesViewDateOrdenedReverse() {
        return getPurchasesView()
                .stream()
                .sorted(
                        Comparator
                                .comparing(PurchaseView::getDate).reversed()
                                .thenComparing(PurchaseView::getId, Comparator.reverseOrder())

                )
                .toList();
    }

    public Optional<Purchase> getById(Integer id) {
        return purchaseRepository.findById(id);
    }

    // Como nós brasileiros usamos a virgula (,) para separar a parte decimal dos
    // número
    // e aqui no Java o BigDecimal o ponto (.) esse method resolve isso!
    String solveComma(String bigDecimanStr) {
        return bigDecimanStr.replace(",", ".");
    }

    public BigDecimal calculateTotalSpentInTime(List<PurchaseView> purchasesView, LocalDate startDate,
            LocalDate endDate) {
        return getPurchasesFromDate(purchasesView, startDate, endDate).stream()
                .map(PurchaseView::getExpense)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalSpent(List<PurchaseView> purchasesView) {
        return purchasesView.stream()
                .map(PurchaseView::getExpense)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

     public BigDecimal calculateTotalTotalInLiters(List<PurchaseView> purchasesView) {
        return purchasesView.stream()
                .map(PurchaseView::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<PurchaseView> getPurchasesFromDate(List<PurchaseView> purchasesView, LocalDate startDate,
            LocalDate endDate) {
        return purchasesView.stream().filter(p -> !p.getDate().isBefore(startDate)
                && !p.getDate().isAfter(endDate)).toList();
    }

}
