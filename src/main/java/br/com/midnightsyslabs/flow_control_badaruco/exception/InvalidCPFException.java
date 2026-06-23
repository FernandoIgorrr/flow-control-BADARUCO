package br.com.midnightsyslabs.flow_control_badaruco.exception;

public class InvalidCPFException extends RuntimeException {
    public InvalidCPFException(String message) {
        super(message);
    }

    public InvalidCPFException() {
        super("CPF inválido.");
    }

}
