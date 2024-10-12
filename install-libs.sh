#!/bin/bash

# Verifica si el directorio libs existe
if [ ! -d "libs" ]; then
    echo "El directorio libs no existe."
    exit 1
fi

# Instala las dependencias de las librerías desde el directorio libs
mvn install:install-file -Dfile=libs/bcmail-jdk16-1.45.jar -DgroupId=org.bouncycastle -DartifactId=bcmail -Dversion=1.45 -Dpackaging=jar

mvn install:install-file -Dfile=libs/commons-logging-1.1.1.jar -DgroupId=commons-logging -DartifactId=commons-logging -Dversion=1.1.1 -Dpackaging=jar

mvn install:install-file -Dfile=libs/MITyCLibAPI-1.1.7.jar -DgroupId=com.example -DartifactId=MITyCLibAPI -Dversion=1.1.7 -Dpackaging=jar

mvn install:install-file -Dfile=libs/MITyCLibCert-1.1.7.jar -DgroupId=com.example -DartifactId=MITyCLibCert -Dversion=1.1.7 -Dpackaging=jar

mvn install:install-file -Dfile=libs/MITyCLibXADES-1.1.7.jar -DgroupId=com.example -DartifactId=MITyCLibXADES -Dversion=1.1.7 -Dpackaging=jar

mvn install:install-file -Dfile=libs/xmlsec-1.4.2-ADSI-1.1.jar -DgroupId=org.apache.xml.security -DartifactId=xmlsec -Dversion=1.4.2-ADSI-1.1 -Dpackaging=jar

mvn install:install-file -Dfile=libs/xstream-1.4.1.jar -DgroupId=com.thoughtworks.xstream -DartifactId=xstream -Dversion=1.4.1 -Dpackaging=jar

echo "Las dependencias se han instalado correctamente."
