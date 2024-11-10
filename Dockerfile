# Usa una imagen base de OpenJDK 8
FROM openjdk:8-jdk-alpine

ARG DOCKER_VERSION

# Establece un directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR desde target al contenedor
COPY target/FirmadorDigital-${DOCKER_VERSION}.jar /app/app.jar

# Copia el archivo application.properties al contenedor
COPY ./application.properties /app/application.properties

# Establece la variable de entorno JAVA_TOOL_OPTIONS para la codificación
ENV JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8

# Expone el puerto 1605 (interno)
EXPOSE 1605

# Comando para ejecutar el JAR
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=/app/application.properties"]
