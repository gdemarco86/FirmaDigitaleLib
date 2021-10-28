package it.gpj.fdl.exceptions;

/**
 * Eccezione generata dalle classi che implementano l'interfaccia IDocumentRetriever
 * nel caso di problemi nel recupero del documento
 * @author Giovanni
 */
public class DocumentRetrievingException extends Exception{

    public DocumentRetrievingException(String message) {
        super(message);
    }

    public DocumentRetrievingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentRetrievingException(Throwable cause) {
        super(cause);
    }
    
}
