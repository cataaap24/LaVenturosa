
## Evidencia de Pruebas de Aceptación

### 4. Generar Reporte
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





