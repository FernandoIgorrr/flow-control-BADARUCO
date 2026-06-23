package br.com.midnightsyslabs.flow_control_badaruco.exception;

public class SpentNotFoundException extends RuntimeException{
      public SpentNotFoundException(String message) {
        super(message);
    }

    public SpentNotFoundException() {
        super("A despesa não foi encontrada no banco de dados!");
    }
    
}
