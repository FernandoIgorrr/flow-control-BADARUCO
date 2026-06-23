package br.com.midnightsyslabs.flow_control.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.stereotype.Service;

@Service
public class UtilsService {
    // Como nós brasileiros usamos a virgula (,) para separar a parte decimal dos
    // número
    // e aqui no Java o BigDecimal o ponto (.) esse method resolve isso!
    public static String solveComma(String bigDecimanStr) {
        return bigDecimanStr.replace(",", ".");
    }

    public static String solveDot(String bigDecimanStr) {
        return bigDecimanStr.replace(".", ",");
    }

    public static String formatPrice(BigDecimal price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        return formatter.format(price);
    }

    public static String formatQuantity(BigDecimal quantity) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.of("pt", "BR"));

        DecimalFormat format = new DecimalFormat("#,##0.###", symbols);
        format.setMaximumFractionDigits(3);
        format.setMinimumFractionDigits(0);

        return format.format(quantity);
    }
}
