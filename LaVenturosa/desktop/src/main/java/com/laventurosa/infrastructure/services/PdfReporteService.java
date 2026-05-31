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

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Colores ───────────────────────────────────────────────────────────────
    private static final BaseColor COLOR_HEADER       = new BaseColor(15,  110,  86);
    private static final BaseColor COLOR_NORMAL_PAR   = new BaseColor(240, 248, 244);
    private static final BaseColor COLOR_NORMAL_IMPAR = BaseColor.WHITE;
    private static final BaseColor COLOR_ADVERTENCIA  = new BaseColor(255, 243, 205);
    private static final BaseColor COLOR_CRITICO      = new BaseColor(248, 215, 218);
    private static final BaseColor COLOR_TEXTO_ADV    = new BaseColor(146,  64,  14);
    private static final BaseColor COLOR_TEXTO_CRIT   = new BaseColor(153,  27,  27);

    // ── Fuentes ───────────────────────────────────────────────────────────────
    private static final Font F_TITULO =
            new Font(Font.FontFamily.HELVETICA, 17, Font.BOLD,   COLOR_HEADER);
    private static final Font F_SUB =
            new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.GRAY);
    private static final Font F_INFO =
            new Font(Font.FontFamily.HELVETICA,  9, Font.NORMAL, BaseColor.BLACK);
    private static final Font F_INFO_I =
            new Font(Font.FontFamily.HELVETICA,  9, Font.ITALIC, BaseColor.BLACK);
    private static final Font F_ENC =
            new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD,   BaseColor.WHITE);
    private static final Font F_CELDA =
            new Font(Font.FontFamily.HELVETICA,  9, Font.NORMAL, BaseColor.BLACK);
    private static final Font F_CELDA_ADV =
            new Font(Font.FontFamily.HELVETICA,  9, Font.BOLD,   COLOR_TEXTO_ADV);
    private static final Font F_CELDA_CRIT =
            new Font(Font.FontFamily.HELVETICA,  9, Font.BOLD,   COLOR_TEXTO_CRIT);
    private static final Font F_PIE =
            new Font(Font.FontFamily.HELVETICA,  8, Font.ITALIC, BaseColor.GRAY);

    @Override
    public void generarReporteMediciones(List<Medicion> mediciones, String rutaSalida) {
        Document doc = new Document(PageSize.A4, 36, 36, 36, 36);

        try (FileOutputStream fos = new FileOutputStream(rutaSalida)) {
            PdfWriter.getInstance(doc, fos);
            doc.open();

            agregarEncabezado(doc, mediciones.size());
            agregarEstadisticas(doc, mediciones);
            agregarTabla(doc, mediciones);
            agregarPie(doc);

            doc.close(); 

            System.out.println("[PDF] Reporte generado exitosamente en: " + rutaSalida);

        } catch (Exception e) {
            throw new RuntimeException("Fallo en la estructura o escritura del PDF", e);
        }
        // ← el finally con doc.close() se elimina
    }

    private void agregarEncabezado(Document doc, int total) throws DocumentException {
        Paragraph titulo = new Paragraph("Reporte de Monitoreo — Laguna La Venturosa", F_TITULO);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);

        Paragraph sub = new Paragraph("Sistema de Monitoreo de Variables Fisicoquímicas", F_SUB);
        sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingAfter(10);
        doc.add(sub);

        String fechaGen = java.time.OffsetDateTime.now(ZoneOffset.of("-05:00"))
                .format(FORMATO_FECHA) + " (COT)";

        doc.add(new Paragraph("Generado: " + fechaGen, F_INFO));
        doc.add(new Paragraph("Total mediciones: " + total, F_INFO));

        Paragraph espacio = new Paragraph(" ");
        espacio.setSpacingAfter(5);
        doc.add(espacio);
    }

    private void agregarEstadisticas(Document doc, List<Medicion> mediciones) throws DocumentException {
        List<String> puntos = List.of("Laguna-Entrada", "Laguna-Produccion", "Laguna-Canio");

        for (String punto : puntos) {
            List<Medicion> filtrado = mediciones.stream()
                    .filter(m -> m.getPuntoMonitoreo() != null && m.getPuntoMonitoreo().equals(punto))
                    .toList();

            if (filtrado.isEmpty()) continue;

            double prom = filtrado.stream().mapToDouble(Medicion::getValor).average().orElse(0);
            double min  = filtrado.stream().mapToDouble(Medicion::getValor).min().orElse(0);
            double max  = filtrado.stream().mapToDouble(Medicion::getValor).max().orElse(0);
            long alertas = filtrado.stream().filter(m -> m.getEstado() != EstadoCriticidad.NORMAL).count();

            Paragraph pEstadistica = new Paragraph(
                    punto
                            + "  —  Promedio: " + String.format("%.2f", prom)
                            + "  |  Mín: "      + String.format("%.2f", min)
                            + "  |  Máx: "      + String.format("%.2f", max)
                            + "  |  Alertas: "  + alertas,
                    F_INFO_I);

            doc.add(pEstadistica);
        }

        Paragraph espacio = new Paragraph(" ");
        espacio.setSpacingAfter(10);
        doc.add(espacio);
    }

    private void agregarTabla(Document doc, List<Medicion> mediciones) throws DocumentException {
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{22f, 25f, 20f, 13f, 20f});

        // Cabeceras
        List<String> cabeceras = List.of("Fecha y Hora (COT)", "Punto de monitoreo", "Variable", "Valor", "Estado");
        for (String enc : cabeceras) {
            PdfPCell c = new PdfPCell(new Phrase(enc, F_ENC));
            c.setBackgroundColor(COLOR_HEADER);
            c.setPadding(6);
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            c.setVerticalAlignment(Element.ALIGN_MIDDLE);
            c.setBorderColor(BaseColor.WHITE);
            tabla.addCell(c);
        }

        // Filas alternas y mapeo de datos reales
        boolean par = false;
        for (Medicion m : mediciones) {
            BaseColor bg   = colorFondo(m.getEstado(), par);
            Font font      = colorFuente(m.getEstado());

            String fechaCOT = m.getFechaHora()
                    .withOffsetSameInstant(ZoneOffset.of("-05:00"))
                    .format(FORMATO_FECHA);

            String variableConUnidad = m.getVariable() != null ? m.getVariable().getNombre() : "N/A";

            agregarCelda(tabla, fechaCOT, bg, font, Element.ALIGN_CENTER);
            agregarCelda(tabla, m.getPuntoMonitoreo(), bg, font, Element.ALIGN_LEFT);
            agregarCelda(tabla, variableConUnidad, bg, font, Element.ALIGN_LEFT);
            agregarCelda(tabla, String.format("%.2f", m.getValor()), bg, font, Element.ALIGN_CENTER);

            String textoEstado = m.getEstado() != null ? m.getEstado().name() : "NORMAL";
            agregarCelda(tabla, textoEstado, bg, font, Element.ALIGN_CENTER);

            par = !par;
        }

        doc.add(tabla);
    }

    private void agregarPie(Document doc) throws DocumentException {
        Paragraph espacio = new Paragraph(" ");
        espacio.setSpacingBefore(15);
        doc.add(espacio);

        Paragraph pie = new Paragraph(
                "Documento generado automáticamente por el Sistema de Monitoreo — Laguna La Venturosa.", F_PIE);
        pie.setAlignment(Element.ALIGN_CENTER);
        doc.add(pie);
    }

    // ── Helpers de Estilo ─────────────────────────────────────────────────────
    private BaseColor colorFondo(EstadoCriticidad estado, boolean par) {
        if (estado == null) return par ? COLOR_NORMAL_PAR : COLOR_NORMAL_IMPAR;
        return switch (estado) {
            case CRITICO     -> COLOR_CRITICO;
            case ADVERTENCIA -> COLOR_ADVERTENCIA;
            default          -> par ? COLOR_NORMAL_PAR : COLOR_NORMAL_IMPAR;
        };
    }

    private Font colorFuente(EstadoCriticidad estado) {
        if (estado == null) return F_CELDA;
        return switch (estado) {
            case CRITICO     -> F_CELDA_CRIT;
            case ADVERTENCIA -> F_CELDA_ADV;
            default          -> F_CELDA;
        };
    }

    private void agregarCelda(PdfPTable tabla, String texto, BaseColor bg, Font font, int alineacion) {
        PdfPCell c = new PdfPCell(new Phrase(texto != null ? texto : "", font));
        c.setBackgroundColor(bg);
        c.setPadding(5);
        c.setHorizontalAlignment(alineacion);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setBorderColor(new BaseColor(220, 220, 220));
        c.setBorderWidth(0.5f);
        tabla.addCell(c);
    }
}
