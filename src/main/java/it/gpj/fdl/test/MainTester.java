package it.gpj.fdl.test;

import eu.europa.esig.dss.cades.CAdESSignatureParameters;
import eu.europa.esig.dss.cades.signature.CAdESService;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import it.gpj.fdl.documents.FileSystemDocumentRetriever;
import it.gpj.fdl.exceptions.DocumentRetrievingException;
import it.gpj.fdl.exceptions.IncorrectPinException;
import it.gpj.fdl.exceptions.NotValidDLLException;
import it.gpj.fdl.exceptions.PKCSException;
import it.gpj.fdl.forms.DSSPrivateKeySelectionForm;
import it.gpj.fdl.models.PKCS11Parameters;
import it.gpj.fdl.models.SignParameters;
import it.gpj.fdl.models.SignResult;
import it.gpj.fdl.sign.CadesPKCS11Signer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore.PasswordProtection;
import java.util.List;
import javax.security.auth.login.LoginException;
import org.apache.commons.lang3.exception.ExceptionUtils;




/**
 * Classe con Main per i test.
 * Contiene esempio di utilizzo di quello che è attualmente l'unico Signer implementato,
 * ossia quello che fa firme CaDes tramite PKCS11
 * @author Giovanni
 */
public class MainTester {
    	private static final String SUN_PKCS11_PROVIDERNAME = "SunPKCS11";

    public static void main(String[] args) {
        // Istanzio un document retriever per ottenere un doc dal file system
        FileSystemDocumentRetriever docRetriever = 
                new FileSystemDocumentRetriever("TestXmlDocument.xml");
        // Istanzio parametri per PKCS11 passando il PIN
        PKCS11Parameters pcksParams = new PKCS11Parameters("24061986");
        // Istanzio i parametri per la firma
        SignParameters signParams = 
                new SignParameters(SignatureLevel.CAdES_BASELINE_B, SignaturePackaging.ENVELOPING, DigestAlgorithm.SHA256);
        signParams.addAdditionalParameter(SignParameters.KnownAdditionalParameters.SignWithExpiredCertificate, true);
        
        // Istanzio il signer per la firma Cades con PCKS11
        CadesPKCS11Signer signer = new CadesPKCS11Signer(docRetriever,pcksParams);
        try {
            // Firmo
            SignResult signResult = signer.signDocument(signParams);
            
            // Salvo documento firmato in p7m
            signResult.saveSignedDocument("TestXmlDocument_signed.p7m");
            
            // Stampe di prova del base64 della sola firma e di tutto il doc
            System.out.println("Signature value : " + signResult.getBase64SignatureValue());
            System.out.println("Signed file: "+ signResult.getBase64SignedDocument());
        } catch (IncorrectPinException ex) {
            ex.printStackTrace();
        } catch (NotValidDLLException ex) {
            ex.printStackTrace();
        } catch (PKCSException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (DocumentRetrievingException ex) {
            ex.printStackTrace();
        }
    }
    
    // Altri vecchi test deliranti...
    public void test() {
//        
//        String configName = "C:\\Progetti\\FirmaSemplice\\TestFirma\\pkcs11.cfg";
//        Provider p = Security.getProvider("SunPKCS11");
//        p = p.configure(configName);
//        Security.addProvider(p);
////        
        try {
            System.out.println(System.getProperty("user.dir"));
            String dllPath = "C:\\Windows\\System32\\bit4xpki.dll";
            //String dllPath = "C:\\Progetti\\FirmaSemplice\\TestFirma\\bit4xpki_1.dll";
            Pkcs11SignatureToken token = new Pkcs11SignatureToken(dllPath, new PasswordProtection("74509726".toCharArray()));

            DSSPrivateKeyEntry privateKey = null;
            List<DSSPrivateKeyEntry> keys = token.getKeys();
            
            DSSPrivateKeySelectionForm formCertificato = new DSSPrivateKeySelectionForm(keys.toArray(new DSSPrivateKeyEntry[]{}));
            privateKey = formCertificato.getSelectedKey();
            
//            for (DSSPrivateKeyEntry entry : keys) {
//                    System.out.println("CERTIFICATO ABBR.: "+entry.getCertificate().getAbbreviation());
//                    System.out.println("CERTIFICATO IS CA.: "+entry.getCertificate().isCA());
//                    System.out.println("CERTIFICATO SUBJECT NAME: "+entry.getCertificate().getCertificate().getSubjectX500Principal().getName());
//                    System.out.println("CERTIFICATO ISSUER: "+entry.getCertificate().getCertificate().getIssuerDN().getName());
//                    System.out.println("CERTIFICATO SUBJECT: "+entry.getCertificate().getCertificate().getSubjectDN().getName());
//                    privateKey = entry;
//            }

            // Preparing parameters for the CAdES signature
            CAdESSignatureParameters parameters = new CAdESSignatureParameters();
            // We choose the level of the signature (-B, -T, -LT, -LTA).
            parameters.setSignatureLevel(SignatureLevel.CAdES_BASELINE_B);
            // We choose the type of the signature packaging (ENVELOPING, DETACHED).
            parameters.setSignaturePackaging(SignaturePackaging.ENVELOPING);
            // We set the digest algorithm to use with the signature algorithm. You must use the
            // same parameter when you invoke the method sign on the token. The default value is
            // SHA256
            parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);

            // We set the signing certificate
            parameters.setSigningCertificate(privateKey.getCertificate());
            // We set the certificate chain
            parameters.setCertificateChain(privateKey.getCertificateChain());
            parameters.setSignWithExpiredCertificate(true);

            // Create common certificate verifier
            CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
            // Create CAdESService for signature
            CAdESService service = new CAdESService(commonCertificateVerifier);

            //DSSDocument toSignDocument = new FileDocument("TestXmlDocument.xml");
            File initialFile = new File("TestXmlDocument.xml");
            InputStream targetStream = new FileInputStream(initialFile);
            InMemoryDocument toSignDocument = new InMemoryDocument(targetStream);
            
            // Get the SignedInfo segment that need to be signed.
            ToBeSigned dataToSign = service.getDataToSign(toSignDocument, parameters);

            // This function obtains the signature value for signed information using the
            // private key and specified algorithm
            DigestAlgorithm digestAlgorithm = parameters.getDigestAlgorithm();
            SignatureValue signatureValue = token.sign(dataToSign, digestAlgorithm, privateKey);
            

            // We invoke the CAdESService to sign the document with the signature value obtained in
            // the previous step.
            DSSDocument signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);
            
            signedDocument.save("TestXmlDocument_signed.p7m");

            System.out.println("Signature value : " + Utils.toBase64(signatureValue.getValue()));
            System.out.println("Signed file: "+ Utils.toBase64(signedDocument.openStream().readAllBytes()));
        } catch (Exception ex) {
            if (ExceptionUtils.indexOfType(ex, LoginException.class) != -1)
                new IncorrectPinException("Incorrect PIN", ex).printStackTrace();
            else
                ex.printStackTrace();
        }
    }
}
