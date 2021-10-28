package it.gpj.fdl.exceptions;

/**
 * Eccezione generata in caso di problemi durante la fase di firma da Signer
 * di tipo PKCS (che usano tale standard per l'interfacciamento con le smart card)
 * @author Giovanni
 */
public class PKCSException extends Exception{

    public PKCSException(String message) {
        super(message);
    }

    public PKCSException(String message, Throwable cause) {
        super(message, cause);
    }

    public PKCSException(Throwable cause) {
        super(cause);
    }
    
}
