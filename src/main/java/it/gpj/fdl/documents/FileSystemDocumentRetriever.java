package it.gpj.fdl.documents;

import it.gpj.fdl.exceptions.DocumentRetrievingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 *
 * @author Giovanni
 * 
 * Implementazione di un Document Retriever che si occupa di recuperare documenti 
 * a partire da file su file system.
 */
public class FileSystemDocumentRetriever implements IDocumentRetriever {

    protected String filePath;
    protected File file;
    
    /**
     * Crea un Document Retriever basato sul file passato
     * @param filePath Path del file a cui associare il document retriever
     */
    public FileSystemDocumentRetriever(String filePath){
        this.filePath = filePath;
        this.file = new File(filePath);
    }
    
    /**
     * Torna l'InputStream corrispondente al file associato a questo Document Retriever
     * @return InputStream corrispondente al file associato a questo Document Retriever
     * @throws DocumentRetrievingException In caso di problemi nel recupero del file o file non esistente
     */
    @Override
    public InputStream retrieveDocument() throws DocumentRetrievingException {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            throw new DocumentRetrievingException(ex);
        }
    }
}
