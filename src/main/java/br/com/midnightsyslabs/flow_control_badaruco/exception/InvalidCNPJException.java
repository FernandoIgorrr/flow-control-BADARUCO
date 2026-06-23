package br.com.midnightsyslabs.flow_control_badaruco.exception;

public class InvalidCNPJException extends RuntimeException {
    public InvalidCNPJException(String message) {
        super(message);
    }

    public InvalidCNPJException() {
        super("CNPJ inválido.");
    }

}
