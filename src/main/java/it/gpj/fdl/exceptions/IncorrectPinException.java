package it.gpj.fdl.exceptions;

/**
 * Eccezione generata durante la fase di firma di un documento in caso il PIN
 * della smart card fornito risulti errato.
 * @author Giovanni
 */
public class IncorrectPinException extends PKCSException{

    public IncorrectPinException(String message) {
        super(message);
    }

    public IncorrectPinException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectPinException(Throwable cause) {
        super(cause);
    }
    
    
}
