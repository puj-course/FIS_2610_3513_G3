package com.example.entregaya.service;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * HU-39 (#398): Servicio para exportar la lista de tareas de un trabajo a PDF.
 * 
 * Genera un documento PDF con:
 * - Nombre del trabajo
 * - Fecha de exportación
 * - Tabla con: nombre, estado, dificultad, responsable y fecha límite de cada tarea
 * - Mensaje informativo si el trabajo no tiene tareas
 */
@Service
public class PdfExportService {

    private static final float MARGIN = 40;
    private static final float ROW_HEIGHT = 20;
    private static final float HEADER_HEIGHT = 25;
    private static final float FONT_SIZE = 9;
    private static final float HEADER_FONT_SIZE = 10;
    private static final float TITLE_FONT_SIZE = 16;
    private static final float SUBTITLE_FONT_SIZE = 11;
    
    // Anchos de columnas (suman el ancho útil de la página)
    private static final float[] COL_WIDTHS = {160, 70, 70, 120, 95}; // nombre, estado, dificultad, responsable, fecha
    
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Genera un PDF con la lista de tareas de un trabajo.
     *
     * @param trabajo El trabajo cuyas tareas se exportarán
     * @param tareas Lista de tareas del trabajo
     * @return byte[] con el contenido del PDF generado
     * @throws IOException si ocurre un error al generar el PDF
     */
    public byte[] generarPdfTareas(Trabajo trabajo, List<Tarea> tareas) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            float yPosition = page.getMediaBox().getHeight() - MARGIN;
            float pageWidth = page.getMediaBox().getWidth() - 2 * MARGIN;

            // === TÍTULO ===
            yPosition = escribirTitulo(content, trabajo, yPosition);

            // === FECHA DE EXPORTACIÓN ===
            yPosition = escribirFechaExportacion(content, yPosition);

            // === LÍNEA SEPARADORA ===
            yPosition -= 10;
            content.setLineWidth(0.5f);
            content.moveTo(MARGIN, yPosition);
            content.lineTo(MARGIN + pageWidth, yPosition);
            content.stroke();
            yPosition -= 20;

            if (tareas == null || tareas.isEmpty()) {
                // === SIN TAREAS ===
                yPosition = escribirSinTareas(content, yPosition);
            } else {
                // === TABLA DE TAREAS ===
                yPosition = dibujarTabla(document, content, tareas, yPosition, page);
            }

            // === PIE DE PÁGINA ===
            escribirPiePagina(content, page);

            content.close();
            document.save(out);
            return out.toByteArray();
        }
    }

    /**
     * Escribe el título del documento con el nombre del trabajo.
     */
    private float escribirTitulo(PDPageContentStream content, Trabajo trabajo, float y) throws IOException {
        // Título principal
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, TITLE_FONT_SIZE);
        content.newLineAtOffset(MARGIN, y);
        content.showText("Reporte de Tareas");
        content.endText();
        y -= 22;

        // Nombre del trabajo
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, SUBTITLE_FONT_SIZE);
        content.newLineAtOffset(MARGIN, y);
        String nombreTrabajo = truncar(trabajo.getNombreTrabajo(), 70);
        content.showText("Trabajo: " + nombreTrabajo);
        content.endText();
        y -= 18;

        return y;
    }

    /**
     * Escribe la fecha y hora de exportación.
     */
    private float escribirFechaExportacion(PDPageContentStream content, float y) throws IOException {
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, FONT_SIZE);
        content.newLineAtOffset(MARGIN, y);
        content.showText("Fecha de exportacion: " + LocalDateTime.now().format(DATETIME_FMT));
        content.endText();
        y -= 14;
        return y;
    }

    /**
     * Escribe un mensaje indicando que el trabajo no tiene tareas.
     */
    private float escribirSinTareas(PDPageContentStream content, float y) throws IOException {
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_OBLIQUE, 11);
        content.newLineAtOffset(MARGIN, y);
        content.showText("Este trabajo no tiene tareas asignadas.");
        content.endText();
        y -= 20;
        return y;
    }

    /**
     * Dibuja la tabla con encabezados y filas de tareas.
     * Maneja saltos de página automáticamente si hay muchas tareas.
     */
    private float dibujarTabla(PDDocument document, PDPageContentStream content,
                                List<Tarea> tareas, float yStart, PDPage currentPage) throws IOException {

        float y = yStart;
        PDPageContentStream cs = content;

        // === ENCABEZADOS ===
        y = dibujarEncabezados(cs, y);

        // === FILAS DE TAREAS ===
        for (Tarea tarea : tareas) {
            // Verificar si necesita nueva página
            if (y < MARGIN + 40) {
                escribirPiePagina(cs, currentPage);
                cs.close();
                
                currentPage = new PDPage(PDRectangle.LETTER);
                document.addPage(currentPage);
                cs = new PDPageContentStream(document, currentPage);
                y = currentPage.getMediaBox().getHeight() - MARGIN;
                
                // Re-dibujar encabezados en la nueva página
                y = dibujarEncabezados(cs, y);
            }

            y = dibujarFilaTarea(cs, tarea, y);
        }

        // Línea final de la tabla
        float tableWidth = 0;
        for (float w : COL_WIDTHS) tableWidth += w;
        cs.setLineWidth(0.5f);
        cs.moveTo(MARGIN, y);
        cs.lineTo(MARGIN + tableWidth, y);
        cs.stroke();

        // Total de tareas
        y -= 20;
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, FONT_SIZE);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText("Total de tareas: " + tareas.size());
        cs.endText();

        // Si el content stream cambió por paginación, debemos cerrar el nuevo
        if (cs != content) {
            escribirPiePagina(cs, currentPage);
            cs.close();
        }

        return y;
    }

    /**
     * Dibuja los encabezados de la tabla.
     */
    private float dibujarEncabezados(PDPageContentStream cs, float y) throws IOException {
        String[] headers = {"Nombre", "Estado", "Dificultad", "Responsable", "Fecha limite"};
        float tableWidth = 0;
        for (float w : COL_WIDTHS) tableWidth += w;

        // Fondo gris para encabezados
        cs.setNonStrokingColor(0.85f, 0.85f, 0.85f);
        cs.addRect(MARGIN, y - HEADER_HEIGHT, tableWidth, HEADER_HEIGHT);
        cs.fill();
        cs.setNonStrokingColor(0, 0, 0);

        // Bordes del encabezado
        cs.setLineWidth(0.5f);
        cs.addRect(MARGIN, y - HEADER_HEIGHT, tableWidth, HEADER_HEIGHT);
        cs.stroke();

        // Texto de encabezados
        float xPos = MARGIN;
        for (int i = 0; i < headers.length; i++) {
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, HEADER_FONT_SIZE);
            cs.newLineAtOffset(xPos + 5, y - 16);
            cs.showText(headers[i]);
            cs.endText();

            // Línea vertical separadora
            if (i < headers.length - 1) {
                xPos += COL_WIDTHS[i];
                cs.moveTo(xPos, y);
                cs.lineTo(xPos, y - HEADER_HEIGHT);
                cs.stroke();
            }
        }

        return y - HEADER_HEIGHT;
    }

    /**
     * Dibuja una fila con los datos de una tarea.
     */
    private float dibujarFilaTarea(PDPageContentStream cs, Tarea tarea, float y) throws IOException {
        float tableWidth = 0;
        for (float w : COL_WIDTHS) tableWidth += w;

        // Borde de la fila
        cs.setLineWidth(0.3f);
        cs.addRect(MARGIN, y - ROW_HEIGHT, tableWidth, ROW_HEIGHT);
        cs.stroke();

        // Datos de la tarea
        String nombre = truncar(tarea.getNombre() != null ? tarea.getNombre() : "-", 30);
        String estado = tarea.getIsCompletada() ? "Completada" : "Pendiente";
        String dificultad = tarea.getDificultad() != null ? tarea.getDificultad().name() : "-";
        String responsables = obtenerResponsables(tarea);
        String fechaLimite = tarea.getFechaFinal() != null ? tarea.getFechaFinal().format(DATE_FMT) : "Sin fecha";

        String[] valores = {nombre, estado, dificultad, responsables, fechaLimite};

        // Separadores verticales y texto
        float xPos = MARGIN;
        for (int i = 0; i < valores.length; i++) {
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA, FONT_SIZE);
            cs.newLineAtOffset(xPos + 5, y - 14);
            cs.showText(valores[i]);
            cs.endText();

            if (i < valores.length - 1) {
                xPos += COL_WIDTHS[i];
                cs.moveTo(xPos, y);
                cs.lineTo(xPos, y - ROW_HEIGHT);
                cs.stroke();
            }
        }

        return y - ROW_HEIGHT;
    }

    /**
     * Escribe el pie de página con el nombre del sistema.
     */
    private void escribirPiePagina(PDPageContentStream cs, PDPage page) throws IOException {
        float footerY = MARGIN - 15;
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 8);
        cs.newLineAtOffset(MARGIN, footerY);
        cs.showText("EntregaYa - Sistema de Gestion de Tareas Colaborativas | Generado automaticamente");
        cs.endText();
    }

    /**
     * Obtiene los nombres de los responsables de una tarea como texto.
     */
    private String obtenerResponsables(Tarea tarea) {
        Set<User> responsables = tarea.getResponsables();
        if (responsables == null || responsables.isEmpty()) {
            return "Sin asignar";
        }
        String nombres = responsables.stream()
                .map(User::getUsername)
                .collect(Collectors.joining(", "));
        return truncar(nombres, 22);
    }

    /**
     * Trunca un texto si excede la longitud máxima.
     */
    private String truncar(String texto, int maxLength) {
        if (texto == null) return "-";
        if (texto.length() <= maxLength) return texto;
        return texto.substring(0, maxLength - 3) + "...";
    }
}
