# 🌊 Venturosa System
### Sistema de Monitoreo de Variables Fisicoquímicas
 
Venturosa System es una solución de ingeniería de software diseñada para el **monitoreo en tiempo real** y la **gestión de datos ambientales** de una laguna. El sistema permite a técnicos ambientales supervisar variables críticas como el pH y el oxígeno disuelto, configurar alertas y consultar históricos de datos para la toma de decisiones informadas.
 
---
 
## Arquitectura del Proyecto
 
El proyecto está construido bajo los principios de **Clean Architecture** y **Arquitectura Hexagonal**, lo que permite un desacoplamiento total entre la lógica de negocio y la infraestructura.
 
| Capa | Descripción |
|------|-------------|
| **Entities (Dominio)** | Objetos de negocio puros: `Medicion`, `Variable`, `Umbral` |
| **Use Cases (Ejemplos)** | Lógica de los procesos del sistema: `VisualizarEstado`, `ConsultarHistorial`, `ConfigurarUmbrales` |
| **Ports (Puertos)** | Interfaces que definen la comunicación entre el núcleo y el exterior |
| **Infrastructure (Adaptadores)** | Implementaciones reales: `PostgresMedicionRepository` y la interfaz gráfica en JavaFX |
 
---
 
## Funcionalidades Principales
 
| ID | Caso de Uso | Descripción |
|----|-------------|-------------|
| UC-01 | **Visualizar Estado de la Laguna** | Presentación visual de los datos más recientes, comparándolos con umbrales para determinar si el estado es *Normal*, *Advertencia* o *Crítico* |
| UC-02 | **Consultar Historial** | Filtrado de mediciones por rangos de fecha y puntos de monitoreo específicos |
| UC-03 | **Generar Reporte** | Generación de documentos que evidencian el comportamiento de las variables fisicoquímicas en un periodo determinado  |
| UC-04 | **Configurar Umbrales** | Permite al técnico establecer los límites lógicos de las variables para cada punto de monitoreo |
| UC-05 | **Configurar Sistema de Alarmas** | Gestión de notificaciones basadas en la criticidad de los datos |
 
---
 
## Stack Tecnológico
 
- **Lenguaje:** Java 17+
- **Interfaz Gráfica:** JavaFX con FXML
- **Base de Datos:** PostgreSQL (alojado en Supabase)
- **Gestión de Dependencias:** Maven
- **Arquitectura:** Clean Architecture
 
---
 
## Configuración de la Base de Datos
 
El sistema se conecta a una base de datos PostgreSQL en la nube. Para configurar la conexión, edite el archivo `src/main/resources/application.properties`:
 
```properties
db.url=jdbc:postgresql://db.supabase.co:5432/postgres
db.user=tu_usuario
db.password=tu_password
```
 
---
 
## Instalación y Ejecución
 
1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/LaVenturosa.git
   ```
2. Asegúrate de tener el **PostgreSQL Driver** incluido en las dependencias de tu IDE.
3. Configura tus credenciales en `application.properties`.
4. Ejecuta la clase `Main` (o el punto de entrada de JavaFX) para iniciar la aplicación.
---
 
## Equipo de Desarrollo
 
Desarrollado por el equipo de **Ingeniería de Sistemas** de la Universidad de los Llanos:
 
| Desarrollador | Rol |
|---------------|-----|
| Martín Pineda Jaramillo | Desarrollador |
| Juan David Navarro Bermúdez | Desarrollador |
| Catalina Pineda Posada | Desarrolladora |
| Kevin Arturo Panesso | Desarrollador |
 
> **Institución:** Universidad de los Llanos (Unillanos)
> **Programa:** Ingeniería de Sistemas
> **Proyecto:** Sistema de Monitoreo de Variables Fisicoquímicas en una Laguna
