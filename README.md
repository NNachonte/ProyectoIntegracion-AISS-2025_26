#  VideoMiner: Integrated Video Aggregator

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen?style=flat-square&logo=springboot)

Este proyecto es un sistema de integración de servicios de vídeo desarrollado para la asignatura **Arquitectura e Integración de Sistemas Software (AISS)**. Su objetivo es unificar la búsqueda y gestión de vídeos provenientes de plataformas descentralizadas (**PeerTube**) y tradicionales (**DailyMotion**) en una única API centralizada.

##  Arquitectura del Sistema

El sistema se divide en tres microservicios principales:

1.  **PeerTube Miner:** Extrae y normaliza datos de instancias de PeerTube.
2.  **DailyMotion Miner:** Extrae y normaliza datos de instancias de DailyMotion.
3.  **VideoMiner (Core):** Actúa como agregador y almacén central de datos, exponiendo una API unificada para el cliente.

---

##  API Endpoints

A continuación se detallan las operaciones mínimas para la comunicación entre módulos y el acceso del cliente.

| Microservicio | URL base | 
| :--- | :--- |
| VideoMiner | `http://localhost:8080` |
| DailyMotionMiner | `http://localhost:8081` |
| PeerTubeMiner | `http://localhost:8082` |

###  VideoMiner (API)
Es el núcleo del sistema. Recibe los datos de los miners y sirve la información al cliente. Estos son los recursos y sus endpoints:

#### Canales

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/channels` | **Recolección:** Recibe canales de los miners y los guarda. (Usado internamente). |
| `GET` | `/channels` | Lista los canales almacenados. |
| `GET` | `/channels/{id}` | Muestra los detalles del canal especificado por su id |

#### Vídeos

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/videos` | Lista los vídeos almacenados. |
| `GET` | `/videos/{id}` | Muestra los detalles del vídeo especificado por su id |

#### Comentarios

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/comments` | Lista los comentarios almacenados |
| `GET` | `/comments/{id}` | Muestra los detalles del comentario especificado por su id |
| `GET` | `/videos/{videoId}/comments` | Muestra los detalles de los comentarios del vídeo especificado por su id. |


#### Subtítulos

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/captions` | Lista los subtítulos almacenados |
| `GET` | `/captions/{id}` | Muestra los detalles de los subtítulos especificados por su id |
| `GET` | `/videos/{videoId}/captions` | Muestra los detalles de los subtítulos del vídeo especificado por su id. |


###  Miners (PeerTube & DailyMotion)
Servicios especializados en la extracción de datos (ETL). `{platform}` es intercambiable por 'peertube' o 'dailymotion'.  

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `{platform}/channels/{id}` | Inicia una búsqueda en la plataforma y envía el canal seleccionado a VideoMiner, que lo guarda. |
#### Parámetros opcionales
`maxVideos`: La operación devolverá el número de vídeos por canal introducido como parámetro. Valor por defecto: 10.

`maxComments`: Solo usable con PeerTubeMiner. La operación devolverá el número de comentarios por video introducido como parámetro. Valor por defecto: 2.

`maxPages`: Solo usable con DailyMotionMiner. Número máximo de páginas de resultados a devolver. Valor por defecto: 2.



| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `{platform}/channels` | Lista todos los canales de la plataforma. |
| `GET` | `{platform}/channels/{id}` | Muestra los detalles del canal especificado con su id. |
| `GET` | `{platform}/videos` | Lista los vídeos de la plataforma. |
| `GET` | `{platform}/videos/{id}` | Muestra los detalles del vídeo especificado con su id. |

---

##  Tecnologías Utilizadas

*   **Framework:** Spring Boot 3.5.13
*   **Gestión de Dependencias:** Maven
*   **Comunicación:** REST (RestTemplate / WebClient)
*   **Documentación API:** Swagger / OpenAPI 
*   **Base de Datos:** H2 / JPA Hibernate

##  Instalación y Uso

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/NNachonte/ProyectoIntegracion-AISS-2025_26.git](https://github.com/NNachonte/ProyectoIntegracion-AISS-2025_26.git)
