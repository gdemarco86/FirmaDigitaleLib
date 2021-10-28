package it.gpj.fdl.models;

import java.util.Arrays;

/**
 * Parametri specifici da passere a firmatori di tipo PKCS11 per indicare i 
 * vari di accesso a Smart Card.
 * @author Giovanni
 */
public class PKCS11Parameters extends GenericParameters{
    // Lista di default delle librerie/dll che verranno usate per l'accesso a SC
    private static final String[] libsListDefault = {"bit4xpki.dll","bit4ipki.dll","incryptoki2.dll","bit4opki.dll"};
    
    public String[] libsList;
    public String pin;
    
    /**
     * Costruisce parametri generici, verranno usate le librerie standard per tentare
     * l'accesso alla SC senza uso di nessun pin
     */
    public PKCS11Parameters(){
        this.libsList = getDefaultLibsList();
    }
    
    /**
     * Costruisce parametri in cui verranno usate le librerie standard per tentare
     * l'accesso alla SC ma utilizzando il pin fornito
     * @param pin pin di accesso alla SC
     */
    public PKCS11Parameters(String pin){
        this.libsList = getDefaultLibsList();
        this.pin = pin;
    }
    
    /**
     * Costruisce dei parametri in cui è possibile indicare la lista di librerie 
     * (dll) e il pin da usare per tentare l'accesso alla SC
     * @param libsList
     * @param pin
     */
    public PKCS11Parameters(String[] libsList, String pin){
        this.libsList = libsList;
        this.pin = pin;
    }
    
    /**
     * Costruisce parametri in cui è possibile indicare la lista di librerie 
     * (dll) da usare per tentare l'accesso alla SC, senza pin
     * @param libsList
     */
    public PKCS11Parameters(String[] libsList){
        this.libsList = libsList;
    }
    
    /**
     * Torna un array contenente la lista dei path completi per le librerie di default.
     * Si assume che le dll siano nella cartella Windows/System32 e verranno usate
     * le librerie indicate nell'array libsListDefault. 
     * @return Lista di path completi delle librerie di default
     */
    public String[] getDefaultLibsList(){
        // TODO: gestire eventuali altri sistemi operativi e leggere le libs da un file di configurazione
        //String oSName = System.getProperty("os.name");
        String winDir = System.getenv("WINDIR") + "\\system32\\";
        return Arrays.stream(libsListDefault).map(l -> winDir + l).toArray(String[]::new);
    }
}
