#  VideoMiner: Integrated Video Aggregator

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen?style=flat-square&logo=springboot)

Este proyecto es un sistema de integración de servicios de vídeo desarrollado para la asignatura **Arquitectura e Integración de Sistemas Software (AISS)**. Su objetivo es unificar la búsqueda y gestión de vídeos provenientes de plataformas descentralizadas (**PeerTube**) y tradicionales (**DailyMotion**) en una única API centralizada.

---

## Documentación interactiva

**Videominer:** http://localhost:8080/videominer/swagger-ui/index.html

**DailyMotion:** http://localhost:8081/swagger-ui/index.html

**PeerTube:** http://localhost:8082/swagger-ui/index.html

---

## Arquitectura del Sistema

El sistema se divide en tres microservicios principales:

1.  **PeerTube Miner:** Extrae y normaliza datos de instancias de PeerTube.
2.  **DailyMotion Miner:** Extrae y normaliza datos de instancias de DailyMotion.
3.  **VideoMiner (Core):** Actúa como agregador y almacén central de datos, exponiendo una API unificada para el cliente.

---

## Seguridad y Autenticación (API Key)

El microservicio principal (**VideoMiner**) está protegido mediante un sistema de clave única (API Key) para garantizar que solo los clientes y Miners autorizados puedan consultar o insertar datos.

* **Cabecera requerida:** Todas las peticiones HTTP a los endpoints protegidos (canales, vídeos, comentarios, etc.) deben incluir una cabecera llamada `X-API-KEY`.
* **Configuración:** La clave maestra se define en el archivo `src/main/resources/application.properties` del proyecto VideoMiner, usando la propiedad:
    ```properties
    videominer.api.key=clave123
    ```
* **Excepciones:** Las rutas de documentación interactiva (`/swagger-ui`, `/v3/api-docs`) y la consola de la base de datos H2 (`/h2-console`) son de acceso público para facilitar el desarrollo y la evaluación.

> **💡 Nota para pruebas (Postman):**
> Para probar los endpoints de VideoMiner, ve a la pestaña **Headers** de tu petición, añade `X-API-KEY` en la columna *Key*, y el valor de tu `application.properties` en la columna *Value*.

---

## API Endpoints

A continuación se detallan las operaciones mínimas para la comunicación entre módulos y el acceso del cliente.

| Microservicio | URL base | 
| :--- | :--- |
| VideoMiner | `http://localhost:8080/videominer` |
| DailyMotionMiner | `http://localhost:8081/dailymotion` |
| PeerTubeMiner | `http://localhost:8082/peertube` |

### VideoMiner (API)
Es el núcleo del sistema. Recibe los datos de los miners y sirve la información al cliente. Estos son los recursos y sus endpoints *(Recuerda usar la cabecera `X-API-KEY`)*:

#### Canales

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/channels` | **Recolección:** Recibe canales de los miners y los guarda. (Usado internamente). |
| `GET` | `/channels` | Lista los canales almacenados. |
| `GET` | `/channels/{id}` | Muestra los detalles del canal especificado por su id. |

#### Vídeos

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/videos` | Lista los vídeos almacenados. |
| `GET` | `/videos/{id}` | Muestra los detalles del vídeo especificado por su id. |

#### Comentarios

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/comments` | Lista los comentarios almacenados. |
| `GET` | `/comments/{id}` | Muestra los detalles del comentario especificado por su id. |
| `GET` | `/videos/{videoId}/comments` | Muestra los detalles de los comentarios del vídeo especificado por su id. |


#### Subtítulos

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/captions` | Lista los subtítulos almacenados. |
| `GET` | `/captions/{id}` | Muestra los detalles de los subtítulos especificados por su id. |
| `GET` | `/videos/{videoId}/captions` | Muestra los detalles de los subtítulos del vídeo especificado por su id. |


### Miners (PeerTube & DailyMotion)
Servicios especializados en la extracción de datos (ETL).   

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/channels/{id}` | Inicia una búsqueda en la plataforma y envía el canal seleccionado a VideoMiner, que lo guarda. |

#### Parámetros opcionales
* `maxVideos`: La operación devolverá el número de vídeos por canal introducido como parámetro. Valor por defecto: 10.
* `maxComments`: Solo usable con PeerTubeMiner. La operación devolverá el número de comentarios por video introducido como parámetro. Valor por defecto: 2.
* `maxPages`: Solo usable con DailyMotionMiner. Número máximo de páginas de resultados a devolver. Valor por defecto: 2.

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/channels` | Lista todos los canales de la plataforma. |
| `GET` | `/channels/{id}` | Muestra los detalles del canal especificado con su id. |
| `GET` | `/videos` | Lista los vídeos de la plataforma. |
| `GET` | `/videos/{id}` | Muestra los detalles del vídeo especificado con su id. |

---

## Tecnologías Utilizadas

* **Framework:** Spring Boot 3.5.13
* **Gestión de Dependencias:** Maven
* **Comunicación:** REST (RestTemplate / WebClient)
* **Documentación API:** Swagger / OpenAPI 
* **Base de Datos:** H2 / JPA Hibernate

## Instalación y Uso

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/NNachonte/ProyectoIntegracion-AISS-2025_26.git](https://github.com/NNachonte/ProyectoIntegracion-AISS-2025_26.git)
```

---

## TwitchMiner

`TwitchMiner` es un microservicio Spring Boot que consume la API de Twitch (Helix) para buscar canales, vídeos y enriquecer datos con clips.

### Obtener credenciales de Twitch

1. Accede a https://dev.twitch.tv/console y regístrate/inicia sesión.
2. Ve a **Applications → Register Your Application** y crea una nueva aplicación:
    - Application Name: `TwitchMiner` (o el que prefieras)
    - OAuth Redirect URL: `http://localhost:8083`
    - Application Category: `Application Integration`
3. Copia el `Client ID` y genera el `Client Secret` (se muestra solo una vez).

### Obtener App Access Token

Ejecuta (reemplaza CLIENT_ID y CLIENT_SECRET):

Linux/Mac/WSL:
```bash
curl -X POST https://id.twitch.tv/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=CLIENT_ID_HERE" \
  -d "client_secret=CLIENT_SECRET_HERE" \
  -d "grant_type=client_credentials"
```

PowerShell:
```powershell
$body = @{ client_id = "CLIENT_ID_HERE"; client_secret = "CLIENT_SECRET_HERE"; grant_type = "client_credentials" } | ConvertTo-Json
Invoke-WebRequest -Uri https://id.twitch.tv/oauth2/token -Method POST -ContentType "application/json" -Body $body | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

Del JSON resultante copia el valor de `access_token`.

### Guardar credenciales (recomendado)

Linux/Mac/WSL (añadir a `~/.bashrc` o `~/.zshrc`):
```bash
export TWITCH_CLIENT_ID="your_client_id_here"
export TWITCH_TOKEN="your_access_token_here"
source ~/.bashrc
```

Windows PowerShell (User):
```powershell
[Environment]::SetEnvironmentVariable("TWITCH_CLIENT_ID","your_client_id_here","User")
[Environment]::SetEnvironmentVariable("TWITCH_TOKEN","your_access_token_here","User")
```

### Verificación

1. Inicia la aplicación `TwitchMiner`.
2. Importa `postman-collection-miners.json` en Postman y ejecuta la colección `TwitchMiner`.
3. Si las peticiones devuelven respuestas válidas, la configuración es correcta.
