package br.com.midnightsyslabs.flow_control_badaruco.exception;

public class RawMaterialNotFoundException extends RuntimeException{
    public RawMaterialNotFoundException(String message){
        super(message);
    }

    public RawMaterialNotFoundException(){
        super("Matéria-Prima / Insumo não encontrado");
    }
}
