# FirmaDigitaleLib
Library for Digital Signature based on the DSS European Lib

Usage example:

```java
// Istanzio un document retriever per ottenere un doc dal file system
FileSystemDocumentRetriever docRetriever = 
        new FileSystemDocumentRetriever("TestXmlDocument.xml");
// Istanzio parametri per PKCS11 passando il PIN
PKCS11Parameters pcksParams = new PKCS11Parameters("24061986");
// Istanzio i parametri per la firma
SignParameters signParams = 
        new SignParameters(SignatureLevel.CAdES_BASELINE_B, SignaturePackaging.ENVELOPING, DigestAlgorithm.SHA256);
signParams.addAdditionalParameter(SignParameters.KnownAdditionalParameters.SignWithExpiredCertificate, true);

// Istanzio il signer per la firma Cades con PKCS11
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
```
