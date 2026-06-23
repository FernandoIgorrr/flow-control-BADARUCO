package br.com.midnightsyslabs.flow_control.exception;

public class ProductionNotFoundException extends RuntimeException {
    public ProductionNotFoundException(String message) {
        super(message);
    }

    public ProductionNotFoundException() {
        super("Produção não encontrada no banco de dados!");
    }
}
