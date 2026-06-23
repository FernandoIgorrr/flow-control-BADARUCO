package br.com.midnightsyslabs.flow_control.ui.controller;

import java.math.BigDecimal;

public record PartnerPurchaseTotals(BigDecimal totalExpense, BigDecimal totalQuantity) {
    public static PartnerPurchaseTotals merge(PartnerPurchaseTotals a, PartnerPurchaseTotals b) {
        return new PartnerPurchaseTotals(
            a.totalExpense().add(b.totalExpense()),
            a.totalQuantity().add(b.totalQuantity())
        );
    }
}