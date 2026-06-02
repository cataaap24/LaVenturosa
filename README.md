
# 🌊 Venturosa System

### Sistema de Monitoreo de Variables Fisicoquímicas

Venturosa System es una solución de ingeniería de software diseñada para el **monitoreo en tiempo real**, **telemetría IoT** y la **gestión de datos ambientales** de una laguna. El sistema permite a técnicos ambientales supervisar variables críticas como el pH y el oxígeno disuelto, configurar alertas automatizadas y consultar históricos de datos para la toma de decisiones informadas.

*<ins>Para esta primera entegra, el sistema solo gestiona la variable de pH.</ins>*

---

##  Ficha de Acceso Rápido (Evaluación Docente)

Para facilitar la revisión y evaluación de esta entrega, a continuación se consolidan los accesos directos a todos los entregables, prototipos, documentación y evidencias de funcionamiento:

| Recurso | Enlace de Acceso | Contenido e Instrucciones de Revisión |
| :--- | :--- | :--- |
| 📄 **Documentación Completa** | [Google Docs (Lectura Directa)](https://docs.google.com/document/d/1KMZWIi8ZRfE38u4rVwSe4gZvG64ljC0VW4xI18SicUY/edit?usp=sharing) <br><br> *Alternativa:* `docs/entregaSoftware.docx` | **Documento principal del proyecto.** Contiene el desarrollo metodológico y técnico estructurado en 11 puntos:<br>1. Introducción \| 2. Modelo del Dominio \| 3. Casos de Uso \| 4. Diagrama de Máquina de Estados \| 5. Arquitectura del Sistema \| 6. Prototipo de Interfaz \| 7. Principios de Diseño Aplicados \| 8. GitHub \| 9. Testing \| 10. Conclusiones \| 11. Referencias.<br><br>💡 **Tip de Navegación:** En Google Docs, active la opción **Ver > Mostrar barra lateral de pestañas y esquemas** para saltar rápidamente entre las secciones. |
| 📋 **Casos de Uso Detallados** | `docs/SoftwareSoloUC-breve.docx` *(en repositorio)* | Documento técnico complementario enfocado exclusivamente en la especificación detallada, flujos (principales/alternos) y diagramas de cada Caso de Uso (UC). |
| 🗄️ **Script de Base de Datos** | `LaVenturosa/bd/init_db.sql` *(en repositorio)* | Script DDl estructurado con el esquema  del sistema (`medicion`, `umbral`, `configuracion_alarma`) listo. |
| 🎨 **Prototipo Navegable** | [Prototipo en Figma](https://www.figma.com/proto/qbxutAtIai3xAZB1z3ycD2/Dise%C3%B1o-Monitoreo?node-id=0-1&t=8RR7y56Ez6YXLyeS-1) | Interfaz gráfica interactiva y propuesta de UX para el sistema de monitoreo. |
| 🎬 **Videos de Pruebas de Aceptación** | [Evidencias en Video (GitHub)](https://github.com/cataaap24/LaVenturosa/blob/main/docs/videos/VIDEOS.md) | **Nota:** Deben visualizarse directamente desde el repositorio de GitHub para verificar la ejecución de los escenarios de prueba. |
| 🌐 **Simulador de Hardware** | [Circuito Vivo en Wokwi](https://wokwi.com/projects/431478408378378241) | Entorno interactivo para simular el funcionamiento del ESP32 y el envío de datos de pH. |
| 📊 **Tablero Kanban de Gestión** | [Ver Tablero del Proyecto (Público)](https://github.com/users/cataaap24/projects/2) | Seguimiento de requerimientos, historias de usuario, asignación de tareas (Martín, Juan, Kevin, Catalina) y flujo de desarrollo en tiempo real. |

> 📌 **Nota para la revisión:** Todos los archivos de documentación (`.docx`) y las evidencias, así como los diagramas en Draw.io se encuentran organizados dentro de la carpeta `/docs` en la raíz de este repositorio para su descarga o lectura directa.

 ---
## Arquitectura del Proyecto
El backend en Java está organizado bajo la estructura de paquetes requerida por la cátedra, implementando los conceptos de **Clean Architecture** mediante las siguientes carpetas principales:

| Carpeta Raíz | Paquetes Internos | Descripción y Ejemplos de Componentes del Proyecto |
| :--- | :--- | :--- |
| **entities** | *(Raíz de la capa)* | **Dominio puro.** Contiene los modelos y objetos con sus reglas lógicas de negocio independientes de la tecnología.<br> - *Componentes:* `Medicion.java`, `Umbral.java`, `Variable.java`, `ConfiguracionAlarma.java` |
| **usecases** | `ports`, `services`, `dto` | **Capa de Aplicación.** Modela las acciones y casos de uso del sistema. <br> - **ports:** Interfaces que definen los contratos (`MedicionRepository`, `NotificacionService`).<br> - **services:** Implementaciones de la lógica de aplicación como `RegistrarMedicionUseCase`. |
| **adapters** | `ui` | **Adaptadores de Interfaz.** Puentes de entrada y salida encargados de interactuar directamente con el usuario final.<br> - *Componentes:* Interfaz gráfica en JavaFX (`MainApp.java`, `MainView.java`) y los páneles de visualización. |
| **infrastructure** | `repositories`, `services` | **Capa de Infraestructura.** Contiene todas las tecnologías concretas, librerías, frameworks y servicios externos que soportan el sistema.<br> - *Componentes:* Conexiones JDBC/JPA para PostgreSQL (Supabase), cliente HTTP para la API de **Resend**, configuraciones generales y caché. |
---

## Módulo de Telemetría e Internet de las Cosas (IoT)

El sistema integra un nodo sensor inalámbrico simulado en **Wokwi** basado en el microcontrolador **ESP32**, el cual actúa como una estación externa encargada de recopilar de manera remota los datos fisicoquímicos en tres puntos estratégicos de la laguna.

### Especificaciones del Hardware
* **Microcontrolador:** ESP32 (NodeMCU DevKit v4).
* **Sensores Simulados:** 3 Potenciómetros lineales calibrados para mapear valores reales de pH (0.00 a 14.00).
* **Puntos de Monitoreo Estáticos:**
  * **P1:** Entrada de la Laguna (`Pin IO32`)
  * **P2:** Zona de Producción (`Pin IO33`)
  * **P3:** Caño de Salida (`Pin IO34`)
* **Actuador:** Botón físico de interrupción manual (`Pin IO12`) con resistencia *Pull-Up* interna.

### Flujo de Transmisión de Datos (Batch)
1. **Sincronización Temporal:** Al encenderse, el ESP32 se conecta a la red inalámbrica y sincroniza su reloj interno con el servidor NTP de Google (`time.google.com`) bajo la zona horaria UTC-5 (Colombia).
2. **Estrategia de Envío Automático:** El dispositivo está programado en base al reloj del sistema para realizar envíos automáticos en tres franjas horarias críticas: **06:00, 12:00 y 18:00**.
3. **Mecanismo de Envío Manual:** El operario en campo puede forzar una transmisión síncrona inmediata en cualquier momento presionando el botón físico del nodo.
4. **Serialización:** Las 3 lecturas son empaquetadas en un arreglo de objetos JSON e inyectadas mediante una petición HTTP POST segura (SSL/HTTPS con cliente seguro) directo al endpoint expuesto por el backend.

> 🌐 **Simulación del Hardware en Vivo:** Puede ver, inspeccionar y ejecutar el circuito de telemetría directamente desde el navegador en el siguiente enlace:  
>  **https://wokwi.com/projects/431478408378378241**


---

## Stack Tecnológico

- **Lenguajes de Programación:** Java 17+ (Backend/Escritorio) y C++ (Microcontrolador ESP32).
- **Interfaz Gráfica:** JavaFX con FXML.
- **Framework Core (API REST):** Spring Boot 3.2.0 (Desplegado en la nube a través de **Render**).
- **Gestor de Notificaciones (Mailing):** API de Resend (`ResendNotificacionService`).
- **Base de Datos:** PostgreSQL (Alojado remotamente en **Supabase**).
- **Gestor de Dependencias:** Maven.
- **Entorno de Simulación de Hardware:** Wokwi Simulator.

---

## Configuración del Sistema

### Base de Datos 
El sistema requiere una base de datos PostgreSQL (remota en Supabase o local). El esquema estructural se encuentra en `bd/init_db.sql` y define el siguiente modelo físico:

```sql
-- Script de inicialización de la Base de Datos
CREATE TABLE IF NOT EXISTS medicion (
    id BIGSERIAL PRIMARY KEY, 
    variable TEXT NOT NULL,
    valor FLOAT8,
    fecha_hora TIMESTAMP,
    estado TEXT,
    punto_monitoreo TEXT
);

CREATE TABLE IF NOT EXISTS umbral (
    id BIGSERIAL PRIMARY KEY, 
    variable VARCHAR(255) NOT NULL,
    punto_monitoreo TEXT,
    min_critico FLOAT8,
    min_advertencia FLOAT8,
    max_advertencia FLOAT8,
    max_critico FLOAT8
);

CREATE TABLE IF NOT EXISTS configuracion_alarma (
    id BIGSERIAL PRIMARY KEY, 
    email_destinatario VARCHAR(255) NOT NULL,
    nivel_notificacion VARCHAR(100),
    activo BOOL
);
```
Para configurar la conexión, edite el archivo `src/main/resources/application.properties`:
```properties
db.url=jdbc:postgresql://db.supabase.co:5432/postgres
db.user=tu_usuario
db.password=tu_password

```

### Nodo Sensor ESP32

El firmware embebido del microcontrolador se comunica con el servidor remoto a través del endpoint batch expuesto por el controlador REST:

```cpp
const char* ssid = "Wokwi-GUEST"; 
const char* serverUrl = "https://laventurosa.onrender.com/api/mediciones/batch";

```

---

## Instalación y Ejecución

1. **Clonar el repositorio:**
```bash
git clone https://github.com/tu-usuario/LaVenturosa.git

```


2. **Cargar el Firmware en el Hardware:** El código fuente del microcontrolador se encuentra en la carpeta `telemetria-esp32/telemetria-esp32.ino`. Puede ejecutarse físicamente o utilizando el archivo de conexiones `diagram.json` en el simulador Wokwi.
3. Asegúrate de tener el **PostgreSQL Driver** incluido en las dependencias de tu IDE a través de Maven.
4. Configura tus credenciales correspondientes en `application.properties`.
5. Ejecuta la clase `Main` (o el punto de entrada de JavaFX) para iniciar la aplicación del sistema.

---

## Equipo de Desarrollo

Desarrollado por el equipo de **Ingeniería de Sistemas** de la Universidad de los Llanos:

| Desarrollador | Rol |
| --- | --- |
| Martín Pineda Jaramillo | Desarrollador |
| Juan David Navarro Bermúdez | Desarrollador |
| Catalina Pineda Posada | Desarrolladora |
| Kevin Arturo Panesso | Desarrollador |

> **Institución:** Universidad de los Llanos (Unillanos)
> **Programa:** Ingeniería de Sistemas
> **Curso:** Ingeniería de Software I
> **Proyecto:** Sistema de Gestión y Telemetría de Variables Fisicoquímicas en una Laguna
