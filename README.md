# Microservicio: ms-firma-digital-samoyed

Este microservicio se encarga de firmar documentos digitales y de encriptar contraseñas. A continuación se documentan los principales endpoints disponibles en el servicio.

## Endpoints

### 1. Firmar Documento

- **Método:** POST
- **URL:** `http://api-firmador.samoyed.test/firmador-digital/api/firmar`

#### Request

```json
{
    "signatureFile": "firma digital en base64",
    "documentXml": "documento XML en base64",
    "passwordEncrypt": true,
    "password": "Su contraseña",
    "iv": "código iv se obtiene en el endpoint de encrypt-password"
}
```

#### Respuesta Exitosa

```json
{
    "message": "Documento firmado exitosamente",
    "payload": {
        "file": "archivo en base 64",
        "entity": "SD - ePass3003 auto"
    },
    "errors": null
}
```

#### Respuesta de Error

```json
{
    "message": "Error al desencriptar la contraseña",
    "errors": {
        "error": "Input byte array has incorrect ending byte at 24"
    }
}
```

#### Descripción
- Si el campo `passwordEncrypt` enviado es `false`, la contraseña debe estar en texto plano y el parámetro `iv` no es obligatorio.

---

### 2. Encriptar Contraseña

- **Método:** POST
- **URL:** `http://api-firmador.samoyed.test/firmador-digital/api/encrypt-password`

#### Request

```json
{
    "password": "hola123=="
}
```

#### Respuesta Exitosa

```json
{
    "message": "Contraseña encriptada",
    "payload": {
        "iv": "LGuAqq24vFg0Gb+vo/4Q6Q==",
        "encryptedPassword": "QYyviA2ZvxcGPfRxmGn4NQ=="
    },
    "errors": null
}
```

#### Respuesta de Error

```json
{
    "message": "Error xyz",
    "errors": {
        "error": "xyz"
    }
}
```

#### Descripción
- Este endpoint codifica la contraseña enviada en texto plano y retorna los parámetros `iv` y `encryptedPassword`.


---

## Instrucciones para Levantar el Microservicio con Docker

Para levantar el microservicio, asegúrate de tener instalado Docker y Docker Compose en tu máquina. Luego, sigue estos pasos:

1. **Construir el proyecto con Maven:**

   Ejecuta el siguiente comando en la raíz del proyecto para compilar el código y empaquetar el microservicio:

   ```bash
   mvn clean package
   ```
2. **Copiar archivo application.propierties.dist a application.propierties y  reemplazar el valor de:**

    ```bash
   encryption.secret-key=clave_en_b64
   ```

3. **Construir la imagen de Docker:**

   Después de compilar el proyecto, construye la imagen de Docker usando Docker Compose:

   ```bash
   docker-compose build
   ```

3. **Levantar el contenedor:**

   Finalmente, ejecuta el siguiente comando para levantar el contenedor en segundo plano:

   ```bash
   docker-compose up -d
   ```

Con estos pasos, el microservicio `ms-firma-digital-samoyed` estará corriendo y podrás acceder a los endpoints documentados.
