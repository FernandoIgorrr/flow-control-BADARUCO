package br.com.midnightsyslabs.flow_control.exception;

public class SaleNotFoundException extends RuntimeException{
     public SaleNotFoundException(String message) {
        super(message);
    }

    public SaleNotFoundException() {
        super("Venda não encontrada no banco de dados!");
    }
}
