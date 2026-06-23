package br.com.midnightsyslabs.flow_control_badaruco.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message) {
        super(message);
    }

    public ClientNotFoundException() {
        super("Cliente não encontrado no banco de dados!");
    }
}
