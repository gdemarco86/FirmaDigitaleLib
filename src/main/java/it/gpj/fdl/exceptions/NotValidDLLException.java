package it.gpj.fdl.exceptions;

/**
 * Eccezione generata durante la fase di firma (in caso di utilizzo dello standard
 * PKCS) se si verificano problemi nel recupero della DLL PKCS indicata per 
 * l'interfacciamento con la smart card.
 * 
 * @author Giovanni
 */
public class NotValidDLLException extends PKCSException{

    public NotValidDLLException(String message) {
        super(message);
    }

    public NotValidDLLException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotValidDLLException(Throwable cause) {
        super(cause);
    }
    
}
