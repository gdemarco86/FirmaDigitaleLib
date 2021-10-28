package it.gpj.fdl.sign;

import it.gpj.fdl.documents.IDocumentRetriever;
import it.gpj.fdl.models.SignResult;
import it.gpj.fdl.models.SignParameters;

/**
 * Classe astratta che rappresenta un Signer generico.
 * @author Giovanni
 */
public abstract class AbstractSigner {
    protected IDocumentRetriever documentRetriever;
    
    /**
     * Crea un Signer associato al document retriever passato
     * @param documentRetriever Oggetto che conterrà la logica per il recupero del doc da firmare
     */
    public AbstractSigner(IDocumentRetriever documentRetriever)
    {
        this.documentRetriever = documentRetriever;
    }
    
    /**
     * Effettua la firma del documento 
     * @param signParameters parametri specifi di firma
     * @return Oggetto SignResult con l'esito dell'operazione di firma
     * @throws Exception in caso di problemi durante la firma; eventuali classi 
     * più specifiche potranno tornare eccezioni più specifiche.
     */
    public abstract SignResult signDocument(SignParameters signParameters) throws Exception;
}
