package it.gpj.fdl.documents;

import it.gpj.fdl.exceptions.DocumentRetrievingException;
import java.io.InputStream;

/**
 * Interfaccia pensata per il recupero di Documenti da firmare. Espone un metodo
 * per fare in modo che chi la implementa dovrà fornire una modalità di recupero
 * del file da firmare.
 * @author Giovanni
 */
public interface IDocumentRetriever {
    
    /**
     * Ritorna InputStream relativo al documento da firmare
     * @return InputStream relativo al documento da firmare
     * @throws DocumentRetrievingException In caso di problemi nel recupero del documento
     */
    public InputStream retrieveDocument() throws DocumentRetrievingException;
}
