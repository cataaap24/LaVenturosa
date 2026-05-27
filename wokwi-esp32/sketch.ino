#include <WiFi.h>
#include <HTTPClient.h>
#include "time.h"
#include <WiFiClientSecure.h>

//Red
const char* ssid = "Wokwi-GUEST"; 
const char* password = "";
const char* serverUrl = "https://laventurosa.onrender.com/api/mediciones/batch";
//Pines
const int PIN_BOTON = 12;
const int PIN_PH1 = 32;
const int PIN_PH2 = 33;
const int PIN_PH3 = 34;
//Hora (ntp)
const char* ntpServer = "pool.ntp.org";
const long  gmtOffset_sec = -5 * 3600; //UTC-5 para Colombia
const int   daylightOffset_sec = 0;

bool botonPresionado = false;
int ultimaHoraEnvioAutomatica = -1; // Para evitar enviar varios envios en mismo minuto

void setup() {
  Serial.begin(115200);
  
  //Configuración del botón con resistencia Pull-Up interna
  pinMode(PIN_BOTON, INPUT_PULLUP);

  Serial.print("Conectando a WiFi");
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\n¡Conectado a WiFi!");

  // Configurar la hora del sistema (Google)
  configTime(gmtOffset_sec, daylightOffset_sec, "time.google.com", "pool.ntp.org");
  Serial.print("Sincronizando hora.");
  
  // Esperar hasta que el ESP32 reciba la hora de internet
  struct tm timeinfo;
  int intentos = 0;
  while (!getLocalTime(&timeinfo) && intentos < 10) {
    intentos++;
    delay(1000);
    Serial.print(".");
  }

  if (intentos >= 10) {
    Serial.println("\n[AVISO] No se pudo sincronizar la hora por NTP.");
    Serial.println("El envío automático fallará, pero el BOTÓN MANUAL seguirá funcionando.");
  } else {
    Serial.println("\n¡Hora sincronizada exitosamente!");
    Serial.printf("Hora actual de Colombia: %02d:%02d:%02d\n", timeinfo.tm_hour, timeinfo.tm_min, timeinfo.tm_sec);
  }
}

void loop() {
  //Comprobación de botón
  int estadoBotonActual = digitalRead(PIN_BOTON);
  //Si el botón está presionado (LOW) y antes no lo estaba
  if (estadoBotonActual == LOW && !botonPresionado) {
    delay(50); 
    if (digitalRead(PIN_BOTON) == LOW) {
      botonPresionado = true; 
      Serial.println("\n[MANUAL] Botón detectado. Preparando envío...");
      enviarBatchMediciones();
    }
  }
  
  //Liberar botón
  if (estadoBotonActual == HIGH && botonPresionado) {
    botonPresionado = false;
  }

  //Envío automático a las 6, 12, 18)
  struct tm timeinfo;
  if (getLocalTime(&timeinfo)) {
    int horaActual = timeinfo.tm_hour;
    int minutoActual = timeinfo.tm_min;

    if ((horaActual == 6 || horaActual == 12 || horaActual == 18) && minutoActual == 0) {
      if (ultimaHoraEnvioAutomatica != horaActual) {
        Serial.printf("\n[AUTOMÁTICO] Son las %02d:00. Iniciando envío programado...\n", horaActual);
        enviarBatchMediciones();
        ultimaHoraEnvioAutomatica = horaActual;
      }
    }
    
    if (minutoActual > 0) {
      ultimaHoraEnvioAutomatica = -1;
    }
  }

  delay(200);
}

void enviarBatchMediciones() {
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("Error: No hay conexión WiFi");
    return;
  }

  //Leer valores analógicos (0 a 4095) y mapear a escala de pH 
  double ph1 = (analogRead(PIN_PH1) * 14.0) / 4095.0;
  double ph2 = (analogRead(PIN_PH2) * 14.0) / 4095.0;
  double ph3 = (analogRead(PIN_PH3) * 14.0) / 4095.0;

  Serial.printf("Lecturas -> P1: %.2f | P2: %.2f | P3: %.2f\n", ph1, ph2, ph3);

  //Construir el JSON en formato Batch
  String jsonPayload = "[";
  jsonPayload += "{\"valor\":" + String(ph1, 2) + ",\"variable\":\"pH\",\"puntoMonitoreo\":\"Laguna-Entrada\"},";
  jsonPayload += "{\"valor\":" + String(ph2, 2) + ",\"variable\":\"pH\",\"puntoMonitoreo\":\"Laguna-Produccion\"},";
  jsonPayload += "{\"valor\":" + String(ph3, 2) + ",\"variable\":\"pH\",\"puntoMonitoreo\":\"Laguna-Canio\"}";
  jsonPayload += "]";

  // Usar cliente seguro para conexiones HTTPS en Render sin validar certificados (para evitar errores en conexiones)
  WiFiClientSecure client;
  client.setInsecure(); 

  HTTPClient http;
  http.begin(client, serverUrl); 
  http.addHeader("Content-Type", "application/json");
  http.setTimeout(30000); 

  Serial.println("Enviando datos al servidor...");
  int httpResponseCode = http.POST(jsonPayload);

  if (httpResponseCode > 0) {
    String response = http.getString();
    Serial.print("Código de respuesta del servidor: ");
    Serial.println(httpResponseCode);
    Serial.print("Respuesta: ");
    Serial.println(response);
  } else {
    Serial.print("Error en el envío POST. Código: ");
    Serial.println(httpResponseCode);
    if (httpResponseCode == -1) {
      Serial.println("Intente despertar el servidor.");
    }
  }

  http.end(); 
}
