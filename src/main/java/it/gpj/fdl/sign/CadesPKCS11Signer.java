package it.gpj.fdl.sign;

import eu.europa.esig.dss.cades.CAdESSignatureParameters;
import eu.europa.esig.dss.cades.signature.CAdESService;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import it.gpj.fdl.documents.IDocumentRetriever;
import it.gpj.fdl.exceptions.DocumentRetrievingException;
import it.gpj.fdl.exceptions.IncorrectPinException;
import it.gpj.fdl.exceptions.NotValidDLLException;
import it.gpj.fdl.exceptions.PKCSException;
import it.gpj.fdl.models.PKCS11Parameters;
import it.gpj.fdl.models.SignParameters;
import it.gpj.fdl.models.SignResult;

/**
 * Classe che rappresenta un Signer che si occupa di effettuare firme di tipo CaDes
 * tramite accesso a smart card secondo lo standard PKCS11.
 * @author Giovanni
 */
public class CadesPKCS11Signer extends AbstractPKCS11Signer{

    /**
     * Crea un Signer con i parametri indicati
     * @param documentRetriever Oggetto che verrà utilizzato per il recupero del documento da firmare
     * @param pkcs11Params Parametri specifici per l'accesso a smart card
     */
    public CadesPKCS11Signer(IDocumentRetriever documentRetriever, PKCS11Parameters pkcs11Params) {
        super(documentRetriever, pkcs11Params);
    }

    /**
     * Effettua la firma vera e propria del documento in base ai parametri forniti
     * @param signParameters parametri di firma
     * @return Oggetto SignResult con il risultato della firma; in caso di errori verranno generate specifiche eccezioni
     * @throws IncorrectPinException in caso di pin della smart card errato
     * @throws NotValidDLLException in caso di dll pkcs11 fornita non valida
     * @throws PKCSException in caso di problemi nell'accesso alla smart card
     * @throws DocumentRetrievingException in caso di problemi nel recupero del documento da firmare
     */
    @Override
    public SignResult signDocument(DSSPrivateKeyEntry key, SignParameters signParameters) 
            throws IncorrectPinException, NotValidDLLException, PKCSException, DocumentRetrievingException {
        // Preparing parameters for the CAdES signature
        CAdESSignatureParameters parameters = new CAdESSignatureParameters();
        // We choose the level of the signature (-B, -T, -LT, -LTA).
        parameters.setSignatureLevel(signParameters.signatureLevel);
        // We choose the type of the signature packaging (ENVELOPING, DETACHED).
        parameters.setSignaturePackaging(signParameters.signaturePackaging);
        // We set the digest algorithm to use with the signature algorithm. You must use the
        // same parameter when you invoke the method sign on the token. The default value is
        // SHA256
        parameters.setDigestAlgorithm(signParameters.digestAltorithm);

        // We set the signing certificate
        parameters.setSigningCertificate(key.getCertificate());
        // We set the certificate chain
        parameters.setCertificateChain(key.getCertificateChain());
        if (signParameters.getAdditionalParameter(SignParameters.KnownAdditionalParameters.SignWithExpiredCertificate))
            parameters.setSignWithExpiredCertificate(true);

        // Create common certificate verifier
        CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier(true);
        // Create CAdESService for signature
        CAdESService service = new CAdESService(commonCertificateVerifier);

        //DSSDocument toSignDocument = new FileDocument("TestXmlDocument.xml");
        InMemoryDocument toSignDocument = new InMemoryDocument(documentRetriever.retrieveDocument());

        // Get the SignedInfo segment that need to be signed.
        ToBeSigned dataToSign = service.getDataToSign(toSignDocument, parameters);

        // This function obtains the signature value for signed information using the
        // private key and specified algorithm
        DigestAlgorithm digestAlgorithm = parameters.getDigestAlgorithm();
        SignatureValue signatureValue = token.sign(dataToSign, digestAlgorithm, key);


        // We invoke the CAdESService to sign the document with the signature value obtained in
        // the previous step.
        DSSDocument signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);

        return new SignResult(signatureValue, signedDocument);
    }
}
