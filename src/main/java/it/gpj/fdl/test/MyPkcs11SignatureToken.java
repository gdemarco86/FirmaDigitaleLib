package it.gpj.fdl.test;

import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.token.PasswordInputCallback;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;

/**
 *
 * @author Giovanni
 */
public class MyPkcs11SignatureToken extends Pkcs11SignatureToken {

    @Override
    public void close() {
        super.close(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Signature getSignatureInstance(String javaSignatureAlgorithm) throws NoSuchAlgorithmException {
        return super.getSignatureInstance(javaSignatureAlgorithm); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected KeyStore.PasswordProtection getKeyProtectionParameter() {
        return super.getKeyProtectionParameter(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String getPkcs11Path() {
        return super.getPkcs11Path(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected KeyStore getKeyStore() throws DSSException {
        return super.getKeyStore(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String escapePath(String pathToEscape) {
        return super.escapePath(pathToEscape); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String buildConfig() {
        return super.buildConfig(); //To change body of generated methods, choose Tools | Templates.
    }

    public MyPkcs11SignatureToken(String pkcs11Path) {
        super(pkcs11Path);
    }

    public MyPkcs11SignatureToken(String pkcs11Path, String extraPkcs11Config) {
        super(pkcs11Path, extraPkcs11Config);
    }

    public MyPkcs11SignatureToken(String pkcs11Path, KeyStore.PasswordProtection password) {
        super(pkcs11Path, password);
    }

    public MyPkcs11SignatureToken(String pkcs11Path, KeyStore.PasswordProtection password, String extraPkcs11Config) {
        super(pkcs11Path, password, extraPkcs11Config);
    }

    public MyPkcs11SignatureToken(String pkcs11Path, PasswordInputCallback callback) {
        super(pkcs11Path, callback);
    }

    public MyPkcs11SignatureToken(String pkcs11Path, PasswordInputCallback callback, String extraPkcs11Config) {
        super(pkcs11Path, callback, extraPkcs11Config);
    }

    public MyPkcs11SignatureToken(String pkcs11Path, KeyStore.PasswordProtection password, int slotId) {
        super(pkcs11Path, password, slotId);
    }

    public MyPkcs11SignatureToken(String pkcs11Path, KeyStore.PasswordProtection password, int slotId, String extraPkcs11Config) {
        super(pkcs11Path, password, slotId, extraPkcs11Config);
    }

    public MyPkcs11SignatureToken(String pkcs11Path, PasswordInputCallback callback, int slotId) {
        super(pkcs11Path, callback, slotId);
    }

    public MyPkcs11SignatureToken(String pkcs11Path, PasswordInputCallback callback, int slotId, String extraPkcs11Config) {
        super(pkcs11Path, callback, slotId, extraPkcs11Config);
    }

    public MyPkcs11SignatureToken(String pkcs11Path, PasswordInputCallback callback, int slotId, int slotListIndex, String extraPkcs11Config) {
        super(pkcs11Path, callback, slotId, slotListIndex, extraPkcs11Config);
    }
    
    @Override
    protected Provider getProvider() {
        String configString = buildConfig();
        //LOG.debug("PKCS11 Config : \n{}", configString);

        Provider provider;
        try {
                provider = Security.getProvider("SunPKCS11");
                // "--" is permitted in the constructor sun.security.pkcs11.Config
                provider = provider.configure("--" + configString);
        } catch (Exception e) {
                throw new DSSException("Unable to instantiate PKCS11 (JDK >= 9)", e);
        }

        if (provider == null) {
                throw new DSSException("Unable to create PKCS11 provider");
        }

        // we need to add the provider to be able to sign later
        Security.addProvider(provider);
        
        return provider;
    }
}
