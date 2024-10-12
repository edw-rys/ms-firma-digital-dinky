package com.edwdev.samoyed.firmador.services;

import org.w3c.dom.Document;
import com.edwdev.samoyed.firmador.dto.FirmarDocumentoDTO;
import com.edwdev.samoyed.firmador.enums.TokensAvailables;
import java.util.Map;
import org.springframework.stereotype.Service;
import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.javasign.EnumFormatoFirma;
import es.mityc.javasign.xml.refs.InternObjectToSign;
import es.mityc.javasign.xml.refs.ObjectToSign;

@Service
public class XAdESBESSignature extends GenericXMLSignature {

    /**
     * <p>
     * Firma el archivo XML
     * </p>
     *
     * @param archivo
     * @param urlOutArchivo
     * @return 
     */
    public Map<String, Object> sign(FirmarDocumentoDTO firmarDocumentoDTO,TokensAvailables token) {
    	this.firmarDocumentoDTO = firmarDocumentoDTO;
        XAdESBESSignature signature = new XAdESBESSignature();
        signature.setFirmarDocumentoDTO(firmarDocumentoDTO); 
        //signature.RESOURCE_TO_SIGN=archivo.getAbsolutePath();
        //signature.SIGN_FILE_NAME=archivo.getName();
        //signature.RESOURCE_TO_SIGN_STRING = archivo;
        //signature.setOUTPUT_DIRECTORY(urlOutArchivo);
        // signature.PKCS12_RESOURCE=PKCS12_RESOURCE;
        // signature.PKCS12_PASSWORD=PKCS12_PASSWORD;
        return signature.execute(token);
        
    }

    public XAdESBESSignature() {
    }

    @Override
    protected DataToSign createDataToSign() {
        DataToSign dataToSign = new DataToSign();
        dataToSign.setXadesFormat(EnumFormatoFirma.XAdES_BES);
        dataToSign.setEsquema(XAdESSchemas.XAdES_132);
        dataToSign.setXMLEncoding("UTF-8");
        // Se añade un rol de firma
       // dataToSign.addClaimedRol(new SimpleClaimedRole("Rol de firma"));                
        dataToSign.setEnveloped(true);
        dataToSign.addObject(new ObjectToSign(new InternObjectToSign("comprobante"), "contenido comprobante", null, "text/xml", null));
        dataToSign.setParentSignNode("comprobante");
        Document docToSign = getDocument(this.firmarDocumentoDTO.getDocumentXml());
        dataToSign.setDocument(docToSign);
        return dataToSign;
    }
}

