package br.com.midnightsyslabs.flow_control.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException() {
        super("Produto não encontrado no banco de dados!");
    }
}
