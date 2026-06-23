package br.com.midnightsyslabs.flow_control.ui.controller;

import java.math.BigDecimal;

public record ProductTotals(BigDecimal totalRevenue, BigDecimal totalWeight) {
    // Método utilitário para somar dois totais durante o reducing
    public static ProductTotals merge(ProductTotals a, ProductTotals b) {
        return new ProductTotals(
            a.totalRevenue().add(b.totalRevenue()),
            a.totalWeight().add(b.totalWeight())
        );
    }
}