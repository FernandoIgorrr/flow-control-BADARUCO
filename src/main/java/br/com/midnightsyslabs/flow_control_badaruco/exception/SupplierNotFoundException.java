package br.com.midnightsyslabs.flow_control_badaruco.exception;

public class SupplierNotFoundException extends RuntimeException {
    public SupplierNotFoundException(String message) {
        super(message);
    }

    public SupplierNotFoundException() {
        super("Fornecedor não encontrado no banco de dados!");
    }
}
