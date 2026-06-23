package br.com.midnightsyslabs.flow_control.ui.utils;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public final class MaskUtils {

    private MaskUtils() {
    }

    public static TextFormatter<String> phoneMask() {
        return createMask("(##) #####-####", 11);
    }

    public static TextFormatter<String> cpfMask() {
        return createMask("###.###.###-##", 11);
    }

    public static TextFormatter<String> cnpjMask() {
        return createMask("##.###.###/####-##", 14);
    }

    private static TextFormatter<String> createMask(String mask, int maxDigits) {

        UnaryOperator<TextFormatter.Change> filter = change -> {

            String oldText = change.getControlText();
            String newText = change.getText();

            // Permite backspace normalmente
            if (newText.isEmpty()) {
                return change;
            }

            // Texto resultante apenas com números
            String digits = (oldText + newText).replaceAll("\\D", "");

            if (digits.length() > maxDigits) {
                return null;
            }

            StringBuilder formatted = new StringBuilder();
            int digitIndex = 0;

            for (char c : mask.toCharArray()) {
                if (c == '#') {
                    if (digitIndex < digits.length()) {
                        formatted.append(digits.charAt(digitIndex++));
                    } else {
                        break;
                    }
                } else {
                    if (digitIndex < digits.length()) {
                        formatted.append(c);
                    }
                }
            }

            // Calcula nova posição do cursor
            int newCaretPos = formatted.length();

            change.setText(formatted.toString());
            change.setRange(0, oldText.length());
            change.selectRange(newCaretPos, newCaretPos);

            return change;
        };

        return new TextFormatter<>(filter);
    }

    public static String applyMask(String value, String mask) {
    if (value == null || value.isEmpty()) {
        return "";
    }

    // Remove tudo que não é número
    String digits = value.replaceAll("\\D", "");

    StringBuilder formatted = new StringBuilder();
    int digitIndex = 0;

    for (char c : mask.toCharArray()) {
        if (c == '#') {
            if (digitIndex < digits.length()) {
                formatted.append(digits.charAt(digitIndex++));
            } else {
                break;
            }
        } else {
            if (digitIndex < digits.length()) {
                formatted.append(c);
            }
        }
    }

    return formatted.toString();
}

}
