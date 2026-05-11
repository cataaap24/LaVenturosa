package com.laventurosa.infrastructure.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.laventurosa.entities.EstadoCriticidad;
import com.laventurosa.entities.Medicion;
import com.laventurosa.usecases.ports.ReporteService;

import java.io.FileOutputStream;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfReporteService implements ReporteService {
    public PdfReporteService() {}

    // Colores por estado
    private static final BaseColor COLOR_NORMAL      = new BaseColor(234, 243, 222);
    private static final BaseColor COLOR_ADVERTENCIA = new BaseColor(250, 238, 218);
    private static final BaseColor COLOR_CRITICO     = new BaseColor(252, 235, 235);
    private static final BaseColor COLOR_HEADER      = new BaseColor(30,  80,  50 );
    private static final BaseColor COLOR_TITULO      = new BaseColor(15,  60,  40 );
    private static final BaseColor COLOR_GRIS        = new BaseColor(120, 120, 120);

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void generarReporteMediciones(List<Medicion> mediciones, String rutaSalida) {
        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(rutaSalida));
            doc.open();

            // ── Fuentes ──────────────────────────────────────────────
            Font fuenteTitulo    = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD,   COLOR_TITULO);
            Font fuenteSubtitulo = new Font(Font.FontFamily.HELVETICA, 9,  Font.NORMAL, COLOR_GRIS);
            Font fuenteCabecera  = new Font(Font.FontFamily.HELVETICA, 9,  Font.BOLD,   BaseColor.WHITE);
            Font fuenteCelda     = new Font(Font.FontFamily.HELVETICA, 8,  Font.NORMAL, BaseColor.BLACK);
            Font fuenteLeyenda   = new Font(Font.FontFamily.HELVETICA, 8,  Font.NORMAL, BaseColor.BLACK);

            // ── Encabezado ────────────────────────────────────────────
            Paragraph titulo = new Paragraph("La Venturosa — Reporte de Mediciones", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            String fechaGen = java.time.OffsetDateTime.now(ZoneOffset.of("-05:00"))
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " (COT)";
            Paragraph subtitulo = new Paragraph("Generado: " + fechaGen + "   |   Total de registros: " + mediciones.size(), fuenteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(16);
            doc.add(subtitulo);

            // ── Tabla de mediciones ───────────────────────────────────
            PdfPTable tabla = new PdfPTable(6);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{1f, 1.8f, 1.2f, 2.8f, 1.8f, 2.2f});

            // Cabecera
            String[] cabeceras = {"ID", "Variable", "Valor", "Fecha/Hora (COT)", "Estado", "Punto"};
            for (String cab : cabeceras) {
                PdfPCell cell = new PdfPCell(new Phrase(cab, fuenteCabecera));
                cell.setBackgroundColor(COLOR_HEADER);
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorderColor(BaseColor.WHITE);
                tabla.addCell(cell);
            }

            // Filas
            for (Medicion m : mediciones) {
                BaseColor colorFila = colorPorEstado(m.getEstado());
                String fechaCOT = m.getFechaHora()
                        .withOffsetSameInstant(ZoneOffset.of("-05:00"))
                        .format(FORMATO_FECHA);

                agregarCelda(tabla, String.valueOf(m.getId()),             colorFila, fuenteCelda, Element.ALIGN_CENTER);
                agregarCelda(tabla, m.getVariable().getNombre(),           colorFila, fuenteCelda, Element.ALIGN_CENTER);
                agregarCelda(tabla, String.format("%.2f", m.getValor()),   colorFila, fuenteCelda, Element.ALIGN_CENTER);
                agregarCelda(tabla, fechaCOT,                              colorFila, fuenteCelda, Element.ALIGN_CENTER);
                agregarCelda(tabla, m.getEstado().name(),                  colorFila, fuenteCelda, Element.ALIGN_CENTER);
                agregarCelda(tabla, m.getPuntoMonitoreo(),                 colorFila, fuenteCelda, Element.ALIGN_LEFT);
            }

            doc.add(tabla);

            // ── Leyenda ───────────────────────────────────────────────
            Paragraph tituloLeyenda = new Paragraph("\nLeyenda de estados:",
                    new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK));
            tituloLeyenda.setSpacingBefore(12);
            doc.add(tituloLeyenda);

            PdfPTable leyenda = new PdfPTable(2);
            leyenda.setWidthPercentage(60);
            leyenda.setWidths(new float[]{1.5f, 5f});
            leyenda.setHorizontalAlignment(Element.ALIGN_LEFT);

            agregarCelda(leyenda, "NORMAL",      COLOR_NORMAL,      fuenteLeyenda, Element.ALIGN_CENTER);
            agregarCelda(leyenda, "pH dentro del rango óptimo (6.5 – 8.5)", COLOR_NORMAL, fuenteLeyenda, Element.ALIGN_LEFT);
            agregarCelda(leyenda, "ADVERTENCIA", COLOR_ADVERTENCIA, fuenteLeyenda, Element.ALIGN_CENTER);
            agregarCelda(leyenda, "pH en zona de alerta",            COLOR_ADVERTENCIA, fuenteLeyenda, Element.ALIGN_LEFT);
            agregarCelda(leyenda, "CRÍTICO",     COLOR_CRITICO,     fuenteLeyenda, Element.ALIGN_CENTER);
            agregarCelda(leyenda, "pH fuera de rango seguro",        COLOR_CRITICO,     fuenteLeyenda, Element.ALIGN_LEFT);
            doc.add(leyenda);

            System.out.println("[PDF] Reporte generado en: " + rutaSalida);

        } catch (Exception e) {
            throw new ExceptionConverter(e);
        } finally {
            doc.close();
        }
    }

    private void agregarCelda(PdfPTable tabla, String texto, BaseColor color, Font fuente, int alineacion) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setBackgroundColor(color);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alineacion);
        cell.setBorderColor(new BaseColor(200, 200, 200));
        cell.setBorderWidth(0.5f);
        tabla.addCell(cell);
    }

    private BaseColor colorPorEstado(EstadoCriticidad estado) {
        return switch (estado) {
            case ADVERTENCIA -> COLOR_ADVERTENCIA;
            case CRITICO     -> COLOR_CRITICO;
            default          -> COLOR_NORMAL;
        };
    }
}
