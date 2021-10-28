package it.gpj.fdl.models;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;

/**
 * Parametri specifici da passere a firmatori per indicare i 
 * parametri di firma.
 * @author Giovanni
 */
public class SignParameters extends GenericParameters {
    public SignatureLevel signatureLevel;
    public SignaturePackaging signaturePackaging;
    public DigestAlgorithm digestAltorithm;
    
    /**
     * Crea dei parametri di firma secondo quanto indicato
     * @param signatureLevel Tipologia di firma
     * @param signaturePackaging Algoritmo per la firma
     * @param digestAltorithm Algoritmo per ottenere il digest da firmare
     */
    public SignParameters(SignatureLevel signatureLevel, SignaturePackaging signaturePackaging, DigestAlgorithm digestAltorithm){
        this.signatureLevel = signatureLevel;
        this.signaturePackaging = signaturePackaging;
        this.digestAltorithm = digestAltorithm;
    }
    
    /**
     * Classe di utilità che raccoglie costanti stringhe rappresentanti chaivi di
     * eventuali parametri aggiuntivi
     */
    public class KnownAdditionalParameters {
        
        /**
         * Chiave per parametro che indica se si vuole consentire la firma anche con
         * certificati scaduti
         */
        public static final String SignWithExpiredCertificate = "SignWithExpiredCertificate";
    }
}
