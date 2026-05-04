# 📺 VideoMiner: Integrated Video Aggregator

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=flat-square&logo=springboot)

Este proyecto es un sistema de integración de servicios de vídeo desarrollado para la asignatura **Arquitectura e Integración de Sistemas Software (AISS)**. Su objetivo es unificar la búsqueda y gestión de vídeos provenientes de plataformas descentralizadas (**PeerTube**) y tradicionales (**DailyMotion**) en una única API centralizada.

## 🏗️ Arquitectura del Sistema

El sistema se divide en tres microservicios principales:

1.  **PeerTube Miner:** Extrae y normaliza datos de instancias de PeerTube.
2.  **DailyMotion Miner:** Extrae y normaliza datos de instancias de DailyMotion.
3.  **VideoMiner (Core):** Actúa como agregador y almacén central de datos, exponiendo una API unificada para el cliente.

---

## 🚀 API Endpoints

A continuación se detallan las operaciones mínimas para la comunicación entre módulos y el acceso del cliente.

### 🧠 VideoMiner (Central API)
Es el núcleo del sistema. Recibe los datos de los miners y sirve la información al cliente.

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/videominer/videos` | **Ingesta:** Recibe vídeos de los miners y los guarda. |
| `GET` | `/videominer/videos` | Lista todos los vídeos almacenados. |
| `GET` | `/videominer/videos/{id}` | Obtiene los detalles de un vídeo específico. |

### 🔍 Miners (PeerTube & DailyMotion)
Servicios especializados en la extracción de datos (ETL). {platform} es intercambiable por 'peertube' o 'dailymotion'. En el caso de PeerTube usar el puerto 8082 y en el de DailyMotion 8081

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `http://localhost:808x/{platform}/channels/{id}` | Inicia una búsqueda en la plataforma y envía el vídeo seleccionado a VideoMiner. |
| `GET` | `http://localhost:808x/{platform}/channels` | Lista todos los canales de la plataforma. |
| `GET` | `http://localhost:808x/{platform}/channels/{id}` | Muestra los detalles del canal especificado con su id. |
| `GET` | `http://localhost:808x/{platform}/videos` | Lista los vídeos de la plataforma. |
| `GET` | `http://localhost:808x/{platform}/videos/{id}` | Muestra los detalles del video especificado con su id. |

---

## 🛠️ Tecnologías Utilizadas

*   **Framework:** Spring Boot 3.x
*   **Gestión de Dependencias:** Maven
*   **Comunicación:** REST (RestTemplate / WebClient)
*   **Documentación API:** Swagger / OpenAPI 
*   **Base de Datos:** H2 / JPA Hibernate

## 📦 Instalación y Uso

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/NNachonte/ProyectoIntegracion-AISS-2025_26.git](https://github.com/NNachonte/ProyectoIntegracion-AISS-2025_26.git)
