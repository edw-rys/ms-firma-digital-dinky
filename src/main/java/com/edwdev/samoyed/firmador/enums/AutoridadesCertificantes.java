package com.edwdev.samoyed.firmador.enums;

public enum AutoridadesCertificantes
{
    ANF("ANF High Assurance Ecuador Intermediate CA", "ANF Autoridad intermedia  EC", "ANFAC AUTORIDAD DE CERTIFICACION ECUADOR C.A.", "EC", "1.3.6.1.4.1.37442"),
    BANCO_CENTRAL("AC BANCO CENTRAL DEL ECUADOR", "ENTIDAD DE CERTIFICACION DE INFORMACION-ECIBCE", "BANCO CENTRAL DEL ECUADOR", "EC", "1.3.6.1.4.1.37947"),
    CONSEJO_JUDICATURA("ENTIDAD DE CERTIFICACION ICERT-EC", "SUBDIRECCION NACIONAL DE SEGURIDAD DE LA INFORMACION DNTICS", "CONSEJO DE LA JUDICATURA", "EC", "1.3.6.1.4.1.43745"),
    SECURITY_DATA("AUTORIDAD DE CERTIFICACION SUB SECURITY DATA", "ENTIDAD DE CERTIFICACION DE INFORMACION", "SECURITY DATA S.A.", "EC", "1.3.6.1.4.1.37746"),
    SECURITY_DATA_SUB_1("AUTORIDAD DE CERTIFICACION SUBCA-1 SECURITY DATA", "ENTIDAD DE CERTIFICACION DE INFORMACION", "SECURITY DATA S.A. 1", "EC", "1.3.6.1.4.1.37746"),
    SECURITY_DATA_SUB_2("AUTORIDAD DE CERTIFICACION SUBCA-2 SECURITY DATA", "ENTIDAD DE CERTIFICACION DE INFORMACION", "SECURITY DATA S.A. 2", "EC", "1.3.6.1.4.1.37746"),
    ANF_ECUADOR_CA1("ANF Ecuador CA1", "ANF Autoridad Raiz Ecuador", "ANFAC Autoridad de Certificacion Ecuador CA", "EC", "1.3.6.1.4.1.18332"),
    UANATACA("UANATACA CA2 2016", "TSP-UANATACA", "UANATACA S.A.", "ES", "1.3.6.1.4.1.47286"),
    ECLIPSOFT("UANATACA CA1 2016", "TSP-UANATACA", "UANATACA S.A.", "ES", "1.3.6.1.4.1.57153"),
    DATILMEDIA("Datil Autoridad de Certificacion Subordinada", "Datilmedia S.A.", "Datilmedia S.A.", "EC", "1.3.6.1.4.1.52643");

    private final String cn;
    private final String ou;
    private final String o;
    private final String c;
    private final String oid;

    AutoridadesCertificantes(String cn, String ou, String o, String c, String oid) {
             this.c = c;
             this.o = o;
             this.cn = cn;
             this.ou = ou;
             this.oid = oid;
    }

    public String getC() {
        return this.c;
    }
    
    public String getCn() {
        return this.cn;
    }
    
    public String getO() {
        return this.o;
    }
    
    public String getOu() {
        return this.ou;
    }
    
    public String getOid() {
        return this.oid;
    }
}

