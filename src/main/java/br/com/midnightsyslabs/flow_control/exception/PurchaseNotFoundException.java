package br.com.midnightsyslabs.flow_control.exception;

public class PurchaseNotFoundException extends RuntimeException {

    public PurchaseNotFoundException(String message) {
        super(message);
    }

    public PurchaseNotFoundException() {
        super("Compra não encontrada no banco de dados!");
    }
}
