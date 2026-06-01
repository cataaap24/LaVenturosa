
## Evidencia de Pruebas de Aceptación

### 1. Flujo normal
Se verifica que el sistema procesa correctamente una medición de pH dentro del rango normal, la persiste en Supabase y refleja el estado actualizado en el desktop sin generar alertas.
#### Precondiciones:
- Backend desplegado en Render y activo.
- Umbrales de pH configurados: Normal [6.8, 7.2].
- Al menos un destinatario configurado en el sistema de alarmas.
#### Pasos ejecutados:
1. Se envía un POST al endpoint /api/mediciones/batch con pH=7.0 en Laguna-Entrada, pH=7.0 en Laguna-Produccion y pH=6.9 en Laguna-Caño.
2. Se verifica la respuesta HTTP 200 del backend.
3. Se abre la aplicación desktop y se navega a "Estado de la Laguna".
4. Se observan las tres tarjetas de sensor.
#### Resultado esperado: 
Las tres tarjetas muestran badge verde "Normal" y los valores de pH correctos. No se recibe ningún email.
<video src="https://github.com/user-attachments/assets/ef7872ea-2068-47a8-a69c-1447d806af13" width="100%" controls></video>

### 2. Medición crítica con notificación
Se verifica que cuando una medición supera los umbrales críticos el sistema genera la alerta, la persiste, notifica al destinatario configurado por email (Resend), y el desktop refleja el estado crítico visualmente.
#### Precondiciones:
- Umbrales configurados: minCrítico=5.0, minAdvert=6.9, maxAdvert=7.2, maxCrítico=9.5.
- Destinatario configurado con nivel "Advertencias y críticas", con email válido.
#### Pasos ejecutados:
1. Se envía un POST al endpoint /api/mediciones/batch con pH=7.0 en Laguna-Entrada, pH=7.0 en Laguna-Produccion y pH=4.2 en Laguna-Caño.
2. Se verifica respuesta HTTP 200 del backend.
3. Se abre el desktop y se navega a "Estado de la Laguna".
4. Se revisa el email del destinatario configurado.
#### Resultado esperado: 
Tarjeta "Caño al río" en rojo con badge "Crítico" y pH=4.20. Email recibido con asunto "[La Venturosa] Alerta Crítico - pH".
<video src="https://github.com/user-attachments/assets/61b03e87-5f38-412a-b429-7b5c6bad4039" width="100%" controls></video>
<img width="1133" height="775" alt="image" src="https://github.com/user-attachments/assets/6a8946af-8cea-4247-a3e8-bfd5786edac7" />

### 3. Consultar historial 
Se verifica que el técnico puede filtrar y visualizar el historial de mediciones por rango de fechas, y que el sistema valida correctamente los parámetros ingresados.
#### Precondiciones:
- Existen mediciones registradas en Supabase para el rango de fechas a consultar.
#### Pasos ejecutados:
1. Se abre el desktop y se navega a "Historial".
2. Se intenta consultar con fecha inicio mayor que fecha fin - se verifica el mensaje de error.
3. Se ingresan fechas válidas con datos existentes y se presiona "Buscar".
4. Se verifica la tabla resultante.
5. Se ingresa un rango válido sin datos y se muestra la tabla vacía.
#### Resultado esperado: 
Para fechas inválidas: mensaje de error claro y tabla vacía. Para rango con datos: tabla con columnas Fecha/Hora, Variable, Valor, Punto de monitoreo y Estado, con los registros del período. Para rango sin datos: tabla vacía.
<video src="https://github.com/user-attachments/assets/ec628d75-1608-49d7-87f8-19defed62aa7" width="100%" controls></video>

### 4. Generar reporte
Se verifica que el sistema genera correctamente un reporte PDF con las mediciones del período seleccionado, incluyendo estadísticas por punto de monitoreo y coloreado por estado de criticidad.
#### Precondiciones:
- Existen mediciones registradas en Supabase en el último mes.
- El técnico tiene permisos de escritura en la carpeta de destino.
#### Pasos ejecutados:
1. Se navega a "Generar reporte" en el desktop.
2. Se dejan las fechas vacías (por defecto último mes) y se presiona "Generar PDF".
3. Se selecciona la ubicación de descarga.
4. Se abre el PDF generado y se verifica su contenido.
5. Se repite el proceso para un rango de fechas específico.
#### Resultado esperado: 
PDF generado en la ubicación elegida. El documento contiene encabezado con nombre del sistema y período, estadísticas de promedio, mínimo y máximo por punto de monitoreo, y tabla de mediciones con filas coloreadas según el estado de la medición. Mensaje "Reporte generado correctamente" en la interfaz.

<video src="https://github.com/user-attachments/assets/a91d370b-a275-4203-a7c4-4bae529b853c" width="100%" controls></video>

### 5. Valor inválido rechazado
Se verifica que el backend rechaza mediciones con valores fuera del rango físico posible para pH (0–14) y que no se persiste ningún dato en Supabase.
### Precondiciones:
- Backend activo en Render.
#### Pasos ejecutados:
- Se envía POST con pH=15.5 en Laguna-Entrada.
- Se verifica la respuesta del backend.
- Se consulta en la base de datos para confirmar que no se guardó la medición.
- Se envía POST con pH=-1.0 y se repite la verificación.
#### Resultado esperado:
Respuesta HTTP 400 con mensaje "Valor X fuera del rango físico de pH". La medición no se almacena. No se genera alerta ni notificación.
<video src="https://github.com/user-attachments/assets/f4806a25-2129-4fd7-a018-453a811a06a5" width="100%" controls></video>

### 6. Gestión dinámica de umbrales
Se verifica que el técnico puede configurar los umbrales de pH desde el desktop, que los cambios se persisten en Supabase y que las mediciones posteriores se evalúan con los nuevos umbrales.
#### Precondiciones: 
- Ninguna configuración previa de umbrales (o umbrales existentes que se van a modificar).
#### Pasos ejecutados:
1. Se navega a "Configurar Umbrales" en el desktop.
Se selecciona la variable pH en el combo.
2. Se intentan guardar valores incoherentes (minCrítico > maxCrítico) y se verifica el error.
3. Se ingresan valores válidos: minCrítico=5.0, minAdvert=6.5, maxAdvert=8.5, maxCrítico=9.5 y se presiona "Guardar".
4. Se verifica el mensaje de confirmación y la actualización del label de umbrales actuales.
5. Se envía una medición con pH=6.0 (ahora en zona advertencia con los nuevos umbrales) y se verifica el estado en el desktop.
#### Resultado esperado:
Para valores incoherentes: botón "Guardar" deshabilitado con mensaje de validación. Para valores válidos: mensaje "Umbrales configurados correctamente" y label actualizado. La medición posterior con pH=6.0 aparece con estado ADVERTENCIA en el desktop.
<video src="https://github.com/user-attachments/assets/cca1f57d-0bb9-42fe-b461-224a09c44672" width="100%" controls></video>


### 7. Configuración y desactivación de alarmas
Se verifica que el técnico puede agregar destinatarios de alarma con diferentes niveles de notificación, que un destinatario configurado con "Solo críticas" no recibe notificaciones de advertencia, y que se puede eliminar una configuración existente.
#### Pasos ejecutados:
1. Se navega a la sección "Sistema de alarmas" en el menú lateral de la aplicación de escritorio.
2. Se intenta agregar un correo electrónico con formato inválido y se verifica que el sistema muestre el mensaje de error correspondiente.
3. Se registra un destinatario con un correo válido configurado inicialmente con el nivel "Advertencias y críticas".
4. Se envía una medición en estado ADVERTENCIA (pH=5.8) a través de Wokwi y se verifica que la notificación por correo llega exitosamente al buzón del destinatario.
5. Se selecciona dicho destinatario en la tabla y se presiona el botón "Eliminar", verificando que la fila desaparece inmediatamente de la interfaz.
6. Se vuelve a registrar el mismo correo electrónico válido, pero esta vez configurándolo estrictamente con el nivel "Solo críticas".
7. Se envía nuevamente una medición en estado ADVERTENCIA (pH=5.8) y se confirma que no llega ninguna notificación al correo, cumpliendo con el filtro del umbral.
8. Se envía una medición en estado CRÍTICO (pH=4.0) y se verifica que, en este caso, la notificación sí llega de inmediato al correo del destinatario.
#### Resultado esperado: 
Email inválido rechazado con mensaje de error. Destinatario con "Solo críticas" no recibe email para ADVERTENCIA pero sí para CRÍTICO. Destinatario con "Advertencias y críticas" recibe notificación en ambos casos. Eliminación refleja el cambio inmediatamente en la tabla.
<img width="1606" height="952" alt="image" src="https://github.com/user-attachments/assets/bd94e27f-5f68-4839-bc69-6f20f617c7ff" />
<video src="https://github.com/user-attachments/assets/8523a7a5-b661-4b54-b800-f75eb1be7092" width="100%" controls></video>








