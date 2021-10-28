package it.gpj.fdl.models;

import java.util.HashMap;

/**
 * Classe generica da estendere per rappresentare dei parametri da passare ad
 * una qualche procedura.
 * Presenta una mappa di possibili parametri aggiuntivi da aggiungere ad eventuali
 * proprietà specifiche di ogni set di parametri.
 * @author Giovanni
 */
public class GenericParameters {
    private HashMap<String, Object> additionalParameters = new HashMap<String, Object>();
    
    public GenericParameters(){
    }
    
    /**
     * Aggiunge un parametro alla lista dei parametri aggiuntivi
     * @param <T> Tipo del parametro
     * @param key Chiave
     * @param value Valore
     */
    public <T> void addAdditionalParameter(String key, T value){
        additionalParameters.put(key, value);
    }
    
    /**
     * Restituisce il parametro aggiuntivo corrispondente alla chiave indicata
     * @param <T> Tipo di ritorno del parametro
     * @param key Chiave
     * @return Parametro corrispondente alla chiave indicata
     */
    public <T> T getAdditionalParameter(String key){
        return (T)additionalParameters.get(key);
    }
}
