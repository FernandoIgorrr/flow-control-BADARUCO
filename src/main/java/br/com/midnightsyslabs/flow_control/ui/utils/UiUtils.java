package br.com.midnightsyslabs.flow_control.ui.utils;

import java.util.function.UnaryOperator;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class UiUtils {
    public static void configureQuantityUnityField(TextField quantityField) {
        UnaryOperator<TextFormatter.Change> quantityFilter = change -> {
            String newText = change.getControlNewText();

            // permite campo vazio (usuário apagando)
            if (newText.isEmpty()) {
                return change;
            }

            // só números inteiros
            if (!newText.matches("\\d+")) {
                return null;
            }

            // valor deve ser > 0
            try {
                int value = Integer.parseInt(newText);
                if (value > 0) {
                    return change;
                }
            } catch (NumberFormatException e) {
                return null;
            }

            return null;
        };

        quantityField.setTextFormatter(new TextFormatter<>(quantityFilter));
        quantityField.setPromptText("1");
    }

    public static void configurePriceField(TextField priceField) {
        UnaryOperator<TextFormatter.Change> priceFilter = change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d*(,\\d{0,2})?")) {
                return change;
            }
            return null;
        };

        priceField.setTextFormatter(new TextFormatter<>(priceFilter));
    }

    public static void configureQuantityField(TextField quantityField) {
        UnaryOperator<TextFormatter.Change> quantityFilter = change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d*(,\\d{0,3})?")) {
                return change;
            }
            return null;
        };

        quantityField.setTextFormatter(new TextFormatter<>(quantityFilter));
    }

    public static void showLabelAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Remove o cabeçalho extra para ficar mais limpo
        alert.setContentText(message);
        alert.showAndWait();
    }
}
