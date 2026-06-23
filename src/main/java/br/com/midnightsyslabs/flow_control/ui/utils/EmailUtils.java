package br.com.midnightsyslabs.flow_control.ui.utils;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public final class EmailUtils {

    private EmailUtils() {}

    public static TextFormatter<String> emailFormatter() {

        UnaryOperator<TextFormatter.Change> filter = change -> {

            String text = change.getControlNewText();

            // Não permite espaços
            if (text.contains(" ")) {
                return null;
            }

            // Limite razoável
            if (text.length() > 254) {
                return null;
            }

            return change;
        };

        return new TextFormatter<>(filter);
    }
}
