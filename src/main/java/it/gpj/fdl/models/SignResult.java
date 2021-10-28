package it.gpj.fdl.models;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.utils.Utils;
import java.io.IOException;
import java.io.InputStream;

/**
 * Classe modello che espone vari metodi di utilità per lavorare sul risultato di un'operazione di firma
 * senza utilizzare le classi specifiche della libreria DSS.
 * @author Giovanni
 */
public class SignResult {
    // Oggetti tornati dalla libreria DSS come risultato della firma
    private SignatureValue signatureValue;
    private DSSDocument signedDocument;
    
    /**
     * Crea un Sign Result con i parametri forniti
     * @param signatureValue oggetti che contiene il valore della firma
     * @param signedDocument oggetto che contiene il documento firmato
     */
    public SignResult(SignatureValue signatureValue, DSSDocument signedDocument){
        this.signatureValue = signatureValue;
        this.signedDocument = signedDocument;
    }
    
    /**
     * Torna il flusso di byte rappresentante la firma vera e proria
     * @return flusso di byte corrispondente alla firma del digest
     */
    public byte[] getSignatureValue(){
        return signatureValue.getValue();
    }

    /**
     * Torna il valore di firma in formato Base64
     * @return valore in base64 corrispondente alla firma del digest
     */
    public String getBase64SignatureValue(){
        return Utils.toBase64(getSignatureValue());
    }

    /**
     * Torna lo stream corrispondente al documento firmato
     * @return Stream associato al documento firmato
     * @throws IOException in caso di errori nell'apertura dello stream
     */
    public InputStream getSignedDocumentStream() throws IOException{
        return signedDocument.openStream();
    }

    /**
     * Torna il flusso di byte corrispondente al documento firmato
     * @return flusso di byte corrispondente al documento firmato
     * @throws IOException in caso di errori nella lettura del file firmato
     */
    public byte[] getSignedDocument() throws IOException{
        byte[] result;
        try (InputStream stream = getSignedDocumentStream()) {
            result = stream.readAllBytes();
        }
        return result;
    }

    /**
     * Torna la rappresentazione base64 del documento firmato
     * @return stringa contenente rappresentazione base64 del documento firmato
     * @throws IOException IOException in caso di errori nella lettura del file firmato
     */
    public String getBase64SignedDocument() throws IOException{
        return Utils.toBase64(getSignedDocument());
    }

    /**
     * Effettua il salvataggio del file firmato sul path indicato
     * @param path path in cui effettuare il salvataggio del file
     * @throws IOException in caso di errori nel salvataggio
     */
    public void saveSignedDocument(String path) throws IOException{
        signedDocument.save(path);
    }
}
