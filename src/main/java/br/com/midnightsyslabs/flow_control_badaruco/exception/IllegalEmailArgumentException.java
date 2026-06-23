package br.com.midnightsyslabs.flow_control_badaruco.exception;

public class IllegalEmailArgumentException extends RuntimeException {
   public IllegalEmailArgumentException(String message) {
       super(message);
   }

   public IllegalEmailArgumentException() {
       super("Formato de email inválido.");
   }

}
