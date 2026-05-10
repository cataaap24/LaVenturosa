# 1. Compilación/Build 
# Usar una imagen de Maven que incluye Java 17 (Temurin) para compilar el proyecto
# alias "build" para poder referenciarla más adelante
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Definir el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo de configuración de Maven (POM) a la carpeta actual
COPY pom.xml .

# Ejecutar comando para descargar todas las dependencias (como Jakarta Mail)
# Se hace por separado para aprovechar la caché de Docker y que las subidas sean más rápidas
RUN mvn dependency:go-offline -q

# Copiar toda tu carpeta de código fuente (src) al contenedor
COPY src ./src

# Compilar el código y generar el archivo ejecutable (.jar)
# -DskipTests: Salta las pruebas unitarias para acelerar el despliegue en Render
# -q: Modo "quiet" para que la consola no se llene de mensajes innecesarios
RUN mvn clean package -DskipTests -q


# 2. Imagen de ejecución/Runtime
# Para que la aplicación sea ligera, usamos una imagen que solo tiene el JRE (entorno de ejecución)
# "alpine" indica que usa una versión de Linux ultra pequeña y segura
FROM eclipse-temurin:17-jre-alpine

# Definir el directorio donde vivirá aplicación
WORKDIR /app

# COpiar el archivo JAR compilado desde la etapa anterior ("build")
# Esto descarta el código fuente y Maven, dejando solo el ejecutable
COPY --from=build /app/target/backend-1.0.0.jar app.jar

# Informar que el contenedor escuchará en el puerto 8080 (estándar Render)
EXPOSE 8080

# Comando que inicia sistema al encender el contenedor
# Ejecuta java -jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]