package com.edwdev.samoyed.firmador.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import com.edwdev.samoyed.firmador.dto.FirmarDocumentoDTO;
import com.edwdev.samoyed.firmador.dto.X500NameGeneral;
import com.edwdev.samoyed.firmador.enums.AutoridadesCertificantes;
import com.edwdev.samoyed.firmador.enums.TokensAvailables;
import com.edwdev.samoyed.firmador.exceptions.GenericException;
import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.FirmaXML;
import es.mityc.javasign.pkstore.IPKStoreManager;
import es.mityc.javasign.pkstore.keystore.KSStore;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;


public abstract class GenericXMLSignature {

	protected FirmarDocumentoDTO firmarDocumentoDTO;
    /**
     * <p>
     * Almacén PKCS12 con el que se desea realizar la firma
     * </p>
     */
    //public String PKCS12_RESOURCE = "/examples/usr0061.p12";

    /**
     * <p>
     * Constraseña de acceso a la clave privada del usuario
     * </p>
     */
    //public String PKCS12_PASSWORD = "miclave";

    /**
     * <p>
     * Directorio donde se almacenará el resultado de la firma
     * </p>
     */
    public String OUTPUT_DIRECTORY;

    public void setOUTPUT_DIRECTORY(String OUTPUT_DIRECTORY) {
        this.OUTPUT_DIRECTORY = OUTPUT_DIRECTORY;
    }

    /**
     * <p>
     * Ejecución del ejemplo. La ejecución consistirá en la firma de los datos
     * creados por el método abstracto <code>createDataToSign</code> mediante el
     * certificado declarado en la constante <code>PKCS12_FILE</code>. El
     * resultado del proceso de firma será almacenado en un fichero XML en el
     * directorio correspondiente a la constante <code>OUTPUT_DIRECTORY</code>
     * del usuario bajo el nombre devuelto por el método abstracto
     * <code>getSignFileName</code>
     * </p>
     */
    protected Map<String, Object> execute(TokensAvailables token) {
    	Map<String, String> errors =new HashMap<>();
        try {
            // Obtencion del gestor de claves
            IPKStoreManager storeManager = null;
            String aliaskey = null;
            KeyStore ks = null;
            String store = "pkcs12";

            ks = KeyStore.getInstance(store);
            
        	if (store == "Windows-MY") {
        		//ks.load(this.getClass().getResourceAsStream(PKCS12_RESOURCE), PKCS12_PASSWORD.toCharArray());
        		ks.load(this.firmarDocumentoDTO.getSignatureInputStream(), this.firmarDocumentoDTO.getPassword().toCharArray());
        		ks.load(null, null);
        		storeManager = new KSStore(ks, new PassStoreKS(this.firmarDocumentoDTO.getPassword()));
        	} else if (store.equals("pkcs12")) {
        		ks.load(this.firmarDocumentoDTO.getSignatureInputStream(), this.firmarDocumentoDTO.getPassword().toCharArray());
        		//ks.load(this.getClass().getResourceAsStream(PKCS12_RESOURCE), PKCS12_PASSWORD.toCharArray());
        		ks.load(null, null);
        		storeManager = new KSStore(ks, new PassStoreKS(this.firmarDocumentoDTO.getPassword()));
        	}

            if (storeManager == null) {
            	throw new GenericException("El gestor de claves no se ha obtenido correctamente", 404, null);
            }

            List<TokensAvailables> tokenList = Arrays.asList(TokensAvailables.values());
            for (TokensAvailables tokensAvailables : tokenList) {
            	aliaskey = selectCertificate(ks, tokensAvailables);	
            	if(aliaskey != null) {
            		token = tokensAvailables;
            		break;
            	}
			}
            if(aliaskey == null) {
            	throw new GenericException("La firma no está disponible para el certificado proporcionado", 404, errors);
            }

            // Obtencion del certificado para firmar. Utilizaremos el primer
            // certificado del almacen.
            X509Certificate certificate = (X509Certificate) ks.getCertificate(aliaskey);
            if (certificate == null) {
                throw new GenericException("No existe ningún certificado para firmar", 404, null);
            }

            // Obtención de la clave privada asociada al certificado
            PrivateKey privateKey = null;
            try {
                KeyStore tmpKs = ks;
                privateKey = (PrivateKey) tmpKs.getKey(aliaskey, this.firmarDocumentoDTO.getPassword().toCharArray());
            } catch (UnrecoverableKeyException ex) {
                Logger.getLogger(GenericXMLSignature.class.getName()).log(Level.SEVERE, null, ex);
                errors.put("error", ex.getMessage());
                throw new GenericException("No se pudo recuperar una clave", 404, errors);
            }

            // Obtención del provider encargado de las labores criptográficas
            Provider provider = storeManager.getProvider(certificate);

            /*
             * Creación del objeto que contiene tanto los datos a firmar como la
             * configuración del tipo de firma
             */
            DataToSign dataToSign = createDataToSign();

            /*
             * Creación del objeto encargado de realizar la firma
             */
            FirmaXML firma = new FirmaXML();

            // Firmamos el documento
            Document docSigned = null;
            try {
                Object[] res = firma.signFile(certificate, dataToSign, privateKey, provider);
                docSigned = (Document) res[0];
            } catch (Exception ex) {
            	errors.put("error", ex.getMessage());
            	throw new GenericException("Error realizando la firma", 404, errors);
            }

            Map<String, Object> result = new HashMap<String, Object>();
            
            result.put("file", Base64.getEncoder().encodeToString(documentToString(docSigned).getBytes()));
            result.put("entiry", token.getId() );
            return result;
        } catch (KeyStoreException ex) {
        	errors.put("error", ex.getMessage());
        	Logger.getLogger(GenericXMLSignature.class.getName()).log(Level.SEVERE, null, ex);
        	throw new GenericException("Error realizando la firma", 404, errors);
        } catch (IOException ex) {
            Logger.getLogger(GenericXMLSignature.class.getName()).log(Level.SEVERE, null, ex);
            errors.put("error", ex.getMessage());
            if(ex.getMessage().contains("password")) {
				throw new GenericException(ex.getMessage(), 400, errors);
			}
            throw new GenericException("Error realizando la firma", 404, errors);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(GenericXMLSignature.class.getName()).log(Level.SEVERE, null, ex);
            errors.put("error", ex.getMessage());
            throw new GenericException("El algoritmo de criptografía que no está disponible en la implementación actual", 404, errors);
        } catch (CertificateNotYetValidException ex) {
            Logger.getLogger(GenericXMLSignature.class.getName()).log(Level.SEVERE, null, ex);
            errors.put("error", ex.getMessage());
            throw new GenericException("El certificado digital que aún no es válido", 404, errors);
        } catch (CertificateException ex) {
            Logger.getLogger(GenericXMLSignature.class.getName()).log(Level.SEVERE, null, ex);
            errors.put("error", ex.getMessage());
            throw new GenericException("El certificado digital que aún no es válido", 404, errors);
        }
    }

    public FirmarDocumentoDTO getFirmarDocumentoDTO() {
		return firmarDocumentoDTO;
	}

	public void setFirmarDocumentoDTO(FirmarDocumentoDTO firmarDocumentoDTO) {
		this.firmarDocumentoDTO = firmarDocumentoDTO;
	}

	/**
     * <p>
     * Crea el objeto DataToSign que contiene toda la información de la firma
     * que se desea realizar. Todas las implementaciones deberán proporcionar
     * una implementación de este método
     * </p>
     *
     * @return El objeto DataToSign que contiene toda la información de la firma
     * a realizar
     */
    protected abstract DataToSign createDataToSign();

    
    private static String documentToString(Document doc) {
    	
    	try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace(); // Maneja excepciones según sea necesario
            throw new GenericException("No se ha podido registrar el archivo xml", 400, null);
        }
    	
    }
    /**
     * <p>
     * Escribe el documento a un fichero. Esta implementacion es insegura ya que
     * dependiendo del gestor de transformadas el contenido podría ser alterado,
     * con lo que el XML escrito no sería correcto desde el punto de vista de
     * validez de la firma.
     * </p>
     *
     * @param document El documento a imprmir
     * @param pathfile El path del fichero donde se quiere escribir.
     */
    @SuppressWarnings("unused")
    private void saveDocumentToFileUnsafeMode(Document document, String pathfile) {
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        try {
            serializer = tfactory.newTransformer();

            serializer.transform(new DOMSource(document), new StreamResult(new File(pathfile)));
        } catch (TransformerException e) {
            System.err.println("Error al salvar el documento");
            e.printStackTrace();
            //System.exit(-1);
        }
    }

    /**
     * <p>
     * Devuelve el <code>Document</code> correspondiente al
     * <code>resource</code> pasado como parámetro
     * </p>
     *
     * @param resource El recurso que se desea obtener
     * @return El <code>Document</code> asociado al <code>resource</code>
     */
    protected Document getDocument(String resource) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //File fXmlFile;
        
        byte[] data = Base64.getDecoder().decode(resource);
        
        // Crear un InputStream desde los bytes decodificados
        InputStream inputStream = new ByteArrayInputStream(data);
        
        //fXmlFile = new File(resource);
        dbf.setNamespaceAware(true);
        try {

            DocumentBuilder dBuilder = dbf.newDocumentBuilder();
            doc = dBuilder.parse(inputStream);
            /*
             doc = dbf.newDocumentBuilder().parse(
             this.getClass().getResourceAsStream(resource));
             * */
        } catch (ParserConfigurationException ex) {
            System.err.println(GenericXMLSignature.class.getName() + " " + ex.getMessage());
        } catch (SAXException ex) {
            System.err.println(GenericXMLSignature.class.getName() + " " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println(GenericXMLSignature.class.getName() + " " + ex.getMessage());
        }

        return doc;
    }

    /**
     * <p>
     * Devuelve el contenido del documento XML correspondiente al
     * <code>resource</code> pasado como parámetro
     * </p> como un <code>String</code>
     *
     * @param resource El recurso que se desea obtener
     * @return El contenido del documento XML como un <code>String</code>
     */
    protected String getDocumentAsString(String resource) {
        Document doc = getDocument(resource);
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        StringWriter stringWriter = new StringWriter();
        try {
            serializer = tfactory.newTransformer();
            serializer.transform(new DOMSource(doc), new StreamResult(stringWriter));
        } catch (TransformerException e) {
            System.err.println("Error al imprimir el documento");
            e.printStackTrace();
            //System.exit(-1);
        }

        return stringWriter.toString();
    }



    public static String selectCertificate(KeyStore keyStore, TokensAvailables tokenSelected) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateExpiredException, CertificateNotYetValidException, CertificateException {
        String aliasSelection = null;
        X509Certificate certificated;
        Enumeration<String> names = keyStore.aliases();

        while (names.hasMoreElements()) {
            String aliasKey = names.nextElement();
            certificated = (X509Certificate) keyStore.getCertificate(aliasKey);
            X500NameGeneral x500Emitter = new X500NameGeneral(certificated.getIssuerDN().getName());
            X500NameGeneral x500Subject = new X500NameGeneral(certificated.getSubjectDN().getName());
            if ((tokenSelected.equals(TokensAvailables.SD_BIOPASS) || tokenSelected.equals(TokensAvailables.SD_EPASS3000)) && (x500Emitter.getCN().contains(AutoridadesCertificantes.SECURITY_DATA.getCn()) || x500Emitter.getCN().contains(AutoridadesCertificantes.SECURITY_DATA_SUB_1.getCn()) || x500Emitter.getCN().contains(AutoridadesCertificantes.SECURITY_DATA_SUB_2.getCn()))) {
                if (AutoridadesCertificantes.SECURITY_DATA.getO().equals(x500Emitter.getO()) && AutoridadesCertificantes.SECURITY_DATA.getC().equals(x500Emitter.getC()) && AutoridadesCertificantes.SECURITY_DATA.getO().equals(x500Subject.getO()) && AutoridadesCertificantes.SECURITY_DATA.getC().equals(x500Subject.getC()))
                    if (certificated.getKeyUsage()[0]) {
                        aliasSelection = aliasKey;
                        break;
                    }
                if (AutoridadesCertificantes.SECURITY_DATA_SUB_1.getO().equals(x500Emitter.getO()) && AutoridadesCertificantes.SECURITY_DATA_SUB_1.getC().equals(x500Emitter.getC()) && AutoridadesCertificantes.SECURITY_DATA_SUB_1.getO().equals(x500Subject.getO()) && AutoridadesCertificantes.SECURITY_DATA_SUB_1.getC().equals(x500Subject.getC()))
                    if (certificated.getKeyUsage()[0]) {
                        aliasSelection = aliasKey;
                        break;
                    }
                if (AutoridadesCertificantes.SECURITY_DATA_SUB_2.getO().equals(x500Emitter.getO()) && AutoridadesCertificantes.SECURITY_DATA_SUB_2.getC().equals(x500Emitter.getC()) && AutoridadesCertificantes.SECURITY_DATA_SUB_2.getO().equals(x500Subject.getO()) && AutoridadesCertificantes.SECURITY_DATA_SUB_2.getC().equals(x500Subject.getC()))
                    if (certificated.getKeyUsage()[0]) {
                        aliasSelection = aliasKey;
                        break;
                    }
                continue;
            }
            if (tokenSelected.equals(TokensAvailables.BCE_ALADDIN) || (tokenSelected.equals(TokensAvailables.BCE_IKEY2032) && x500Emitter.getCN().contains(AutoridadesCertificantes.BANCO_CENTRAL.getCn()))) {
                if (x500Emitter.getO().contains(AutoridadesCertificantes.BANCO_CENTRAL.getO()) && AutoridadesCertificantes.BANCO_CENTRAL.getC().equals(x500Emitter.getC()) && x500Subject.getO().contains(AutoridadesCertificantes.BANCO_CENTRAL.getO()) && AutoridadesCertificantes.BANCO_CENTRAL.getC().equals(x500Subject.getC()))
                    if (certificated.getKeyUsage()[0]) {
                        aliasSelection = aliasKey;
                        break;
                    }
                continue;
            }
            if (tokenSelected.equals(TokensAvailables.ANF1) && x500Emitter.getCN().contains(AutoridadesCertificantes.ANF.getCn())) {
                if (AutoridadesCertificantes.ANF.getO().equals(x500Emitter.getO()) && AutoridadesCertificantes.ANF.getC().equals(x500Emitter.getC()) && AutoridadesCertificantes.ANF.getC().equals(x500Subject.getC()))
                    if (certificated.getKeyUsage()[0]) {
                        aliasSelection = aliasKey;
                        break;
                    }
                continue;
            }
            if (tokenSelected.equals(TokensAvailables.ANF1) && x500Emitter.getCN().contains(AutoridadesCertificantes.ANF_ECUADOR_CA1.getCn())) {
                if (AutoridadesCertificantes.ANF_ECUADOR_CA1.getO().equals(x500Emitter.getO()) && AutoridadesCertificantes.ANF_ECUADOR_CA1.getC().equals(x500Emitter.getC()) && AutoridadesCertificantes.ANF_ECUADOR_CA1.getC().equals(x500Subject.getC()))
                    if (certificated.getKeyUsage()[0]) {
                        aliasSelection = aliasKey;
                        break;
                    }
                continue;
            }
            if (tokenSelected.equals(TokensAvailables.KEY4_CONSEJO_JUDICATURA) && x500Emitter.getCN().contains(AutoridadesCertificantes.CONSEJO_JUDICATURA.getCn())) {
                if (x500Emitter.getO().contains(AutoridadesCertificantes.CONSEJO_JUDICATURA.getO()) && AutoridadesCertificantes.CONSEJO_JUDICATURA.getC().equals(x500Emitter.getC()) && AutoridadesCertificantes.CONSEJO_JUDICATURA.getC().equals(x500Subject.getC()))
                    if (certificated.getKeyUsage()[0]) {
                        aliasSelection = aliasKey;
                        break;
                    }
                continue;
            }
            if (tokenSelected.equals(TokensAvailables.TOKENME_UANATACA) && x500Emitter.getCN().contains(AutoridadesCertificantes.UANATACA.getCn())) {
                if (x500Emitter.getO().contains(AutoridadesCertificantes.UANATACA.getO()) && AutoridadesCertificantes.UANATACA.getC().equals(x500Emitter.getC()))
                    if (certificated.getKeyUsage()[0]) {
                        aliasSelection = aliasKey;
                        break;
                    }
                continue;
            }
            if (tokenSelected.equals(TokensAvailables.Eclipsoft) && x500Emitter.getCN().contains(AutoridadesCertificantes.ECLIPSOFT.getCn())) {
                if (x500Emitter.getO().contains(AutoridadesCertificantes.ECLIPSOFT.getO()) && AutoridadesCertificantes.ECLIPSOFT.getC().equals(x500Emitter.getC()))
                    if (certificated.getKeyUsage()[0]) {
                        aliasSelection = aliasKey;
                        break;
                    }
                continue;
            }
            if (tokenSelected.equals(TokensAvailables.DATILMEDIA) && x500Emitter.getCN().contains(AutoridadesCertificantes.DATILMEDIA.getCn()))
                if (AutoridadesCertificantes.DATILMEDIA.getO().equals(x500Emitter.getO()) && AutoridadesCertificantes.DATILMEDIA.getC().equals(x500Emitter.getC()) && AutoridadesCertificantes.DATILMEDIA.getC().equals(x500Subject.getC()))
                    if (certificated.getKeyUsage()[0]) {
                        aliasSelection = aliasKey;
                        break;
                    }
        }
        return aliasSelection;
    }
}

