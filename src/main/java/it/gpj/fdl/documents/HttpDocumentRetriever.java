/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.gpj.fdl.documents;

import it.gpj.fdl.exceptions.DocumentRetrievingException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 * @author Giovanni
 */
public class HttpDocumentRetriever implements IDocumentRetriever{
    
    protected String url;
    
    public HttpDocumentRetriever(String url){
        this.url = url;
    }
    
    /**
     * Torna l'InputStream corrispondente al file associato a questo Document Retriever
     * @return InputStream corrispondente al file associato a questo Document Retriever
     * @throws DocumentRetrievingException In caso di problemi nel recupero del file o file non esistente
     */
    @Override
    public InputStream retrieveDocument() throws DocumentRetrievingException {
        try {
            return new BufferedInputStream(new URL(new URI(url).getPath()).openStream());
        } catch (IOException | URISyntaxException ex) {
            throw new DocumentRetrievingException(ex);
        }
    }
}
