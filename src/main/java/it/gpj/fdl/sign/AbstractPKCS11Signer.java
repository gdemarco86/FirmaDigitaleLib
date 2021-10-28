package it.gpj.fdl.sign;

import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
//import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import it.gpj.fdl.documents.IDocumentRetriever;
import it.gpj.fdl.exceptions.DocumentRetrievingException;
import it.gpj.fdl.exceptions.IncorrectPinException;
import it.gpj.fdl.exceptions.NotValidDLLException;
import it.gpj.fdl.exceptions.PKCSException;
import it.gpj.fdl.forms.DSSPrivateKeySelectionForm;
import it.gpj.fdl.models.PKCS11Parameters;
import it.gpj.fdl.models.SignParameters;
import it.gpj.fdl.models.SignResult;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.stream.Collectors;
import javax.security.auth.login.FailedLoginException;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Classe astratta che rappresenta un Signer che si occupa di effettuare firme 
 * tramite accesso a smart card secondo lo standard PKCS11.
 * Contiene implementazione dei metodi per l'accesso e recupero dei token dalla
 * smart card in modo che gli specifici Signer che la implementeranno possano
 * riutilizzarli
 * @author Giovanni
 */
public abstract class AbstractPKCS11Signer extends AbstractSigner {

    /**
     *
     */
    protected PKCS11Parameters pkcs11Params;

    /**
     * Token rappresentante il "flusso" con la chiavetta da cui ottenere certificati
     * e chiavi private astratte
     */
    protected Pkcs11SignatureToken token;
    
    /**
     * Costruisce un Signer in base ai parametri indicati
     * @param documentRetriever Oggetto che verrà utilizzato per il recupero del documento da firmare
     * @param pkcs11Params Parametri specifici per l'accesso a smart card
     */
    public AbstractPKCS11Signer(IDocumentRetriever documentRetriever, PKCS11Parameters pkcs11Params) {
        super(documentRetriever);
        this.pkcs11Params = pkcs11Params;
    }

    /**
     * Effettua la firma vera e propria del documento
     * @param signParameters parametri di firma
     * @return Oggetto SignResult con il risultato della firma; in caso di errori verranno generate specifiche eccezioni
     * @throws IncorrectPinException in caso di pin della smart card errato
     * @throws NotValidDLLException in caso di dll pkcs11 fornita non valida
     * @throws PKCSException in caso di problemi nell'accesso alla smart card
     * @throws DocumentRetrievingException in caso di problemi nel recupero del documento da firmare
     */
    @Override
    public SignResult signDocument(SignParameters signParameters) 
            throws IncorrectPinException, NotValidDLLException, PKCSException, DocumentRetrievingException {
        DSSPrivateKeyEntry key = getKeyFromSmartCard();
        
        return signDocument(key, signParameters);
    }
    
    /**
     * Metodo astratto che si occupa di effettuare la firma vera e propria ricevendo
     * sia i parametri di firma che la chiave. Dovrà essere implementato dalle specifiche
     * classi che si occuperanno di implementare specifici tipi di firme
     * @param key astrazione della chiave privata da utilizzare nella firma
     * @param signParameters parametri di firma
     * @return Oggetto SignResult con il risultato dell'operazione di firma
     * @throws IncorrectPinException in caso di pin della smart card errato
     * @throws NotValidDLLException in caso di dll pkcs11 fornita non valida
     * @throws PKCSException in caso di problemi nell'accesso alla smart card
     * @throws DocumentRetrievingException in caso di problemi nel recupero del documento da firmare
     */
    protected abstract SignResult signDocument(DSSPrivateKeyEntry key, SignParameters signParameters) 
            throws IncorrectPinException, NotValidDLLException, PKCSException, DocumentRetrievingException;
    
    /**
     * Inizializza la smart card e recupera la lista di chiavi/certificati
     * @return Lista delle astrazioni delle chiavi private presenti nella smart card
     * @throws IncorrectPinException in caso di pin della smart card errato
     * @throws NotValidDLLException in caso di dll pkcs11 fornita non valida
     * @throws PKCSException in caso di problemi nell'accesso alla smart card
     */
    protected List<DSSPrivateKeyEntry> initializeSmartCard() throws IncorrectPinException, NotValidDLLException, PKCSException{
        
        List<DSSPrivateKeyEntry> keys = null;
        String errorMessage = "";
        // Provo l'accesso con tutte le librerie indicate nei parametri
        for (String libsList : pkcs11Params.libsList) {
            if (pkcs11Params.pin != null) {
                token = new Pkcs11SignatureToken(libsList, new KeyStore.PasswordProtection(pkcs11Params.pin.toCharArray()));
            } else {
                token = new Pkcs11SignatureToken(libsList);
            }
            
            try{
                // Tento la lettura delle chiavi, filtrando solo sulle chiavi di firma
                keys = token.getKeys().stream().filter(k -> isSigningKey(k)).collect(Collectors.toList());
            } catch(DSSException dssException){
                // Se l'errore è relativo al fatto che il pin è sbagliato, torno apposita eccezione
                if (ExceptionUtils.indexOfType(dssException, FailedLoginException.class) != -1)
                    throw new IncorrectPinException("Incorrect PIN", dssException);
                // Altrimenti aggiungo nel messaggio di errore l'errore specifico avuto con questa libreria,
                // ma non genero eccezione in modo che il ciclo continui e venga tentato
                // accesso con le altre librerie
                else{
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    dssException.printStackTrace(pw);
                    errorMessage += "Lib "+libsList+": "+sw.toString()+"\n";
                }
//                else {
//                    if (ExceptionUtils.indexOfType(dssException, ProviderException.class) == -1)
//                        throw new PKCSException("Incorrect PIN", dssException);
//                }
            }
            // Se la lettura è andata a buon fine, mi posso fermare
            if (keys != null)
                break;
        }
        // Se non sono riuscito a leggere le chiavi con nessuna libreria, genero eccezione
        // con messaggio che ho composto che contiene la lista di tutti gli errori avuti da ogni libreria
        if (keys == null){
            throw new NotValidDLLException("PKCS dll not found.\n"+errorMessage);
        }
        return keys;
    }
    
    /**
     * Recupera la chave da utilizzare per la firma dalla smart card. In caso di lettura di più
     * chiavi verrà chiesto all'utente tramite apposita form quale usare.
     * @return chiave da utilizzare per la firma
     * @throws IncorrectPinException in caso di pin della smart card errato
     * @throws NotValidDLLException in caso di dll pkcs11 fornita non valida
     * @throws PKCSException in caso di problemi nell'accesso alla smart card
     */
    protected DSSPrivateKeyEntry getKeyFromSmartCard() throws IncorrectPinException, NotValidDLLException, PKCSException{
        List<DSSPrivateKeyEntry> keys = initializeSmartCard();
        DSSPrivateKeyEntry key;
        if (keys.size() == 1)
            return keys.get(0);
        else {
            try {
                DSSPrivateKeySelectionForm formCertificato = new DSSPrivateKeySelectionForm(keys.toArray(new DSSPrivateKeyEntry[]{}));
                return formCertificato.getSelectedKey();
            } catch (IOException ex){
                throw new PKCSException(ex);
            }catch (CertificateException ex){
                throw new PKCSException(ex);
            }
        }
    }

    /**
     * Metodo di utilità per valutare se la chiave fornita è una chiave di firma
     * @param key chiave da verificare
     * @return true se la chiave è di firma, false altrimenti
     */
    protected boolean isSigningKey(DSSPrivateKeyEntry key){
        /*
        Da DOC DSS: tramite keyUsage si ha array di byte il cui significato è il seguente
        KeyUsage ::= BIT STRING {
            digitalSignature        (0),
            nonRepudiation          (1),
            keyEncipherment         (2),
            dataEncipherment        (3),
            keyAgreement            (4),
            keyCertSign             (5),
            cRLSign                 (6),
            encipherOnly            (7),
            decipherOnly            (8) }
        Le chiavi di firma sono quelle che hanno nonRepudiation = true
        */
        return key.getCertificate().getCertificate().getKeyUsage()[1] == true;
    }
}
