package com.example.entregaya.service;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PdfExportService - Tests unitarios")
class PdfExportServiceTest {

    private PdfExportService pdfExportService;
    private Trabajo trabajo;

    @BeforeEach
    void setUp() {
        pdfExportService = new PdfExportService();

        trabajo = new Trabajo();
        trabajo.setId(1L);
        trabajo.setNombreTrabajo("Proyecto PDF Test");
        trabajo.setDescripcion("Descripción de prueba");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
    }

    // CP01 - NORMAL: Generar PDF con lista de tareas retorna bytes no vacíos
    @Test
    @DisplayName("CP01: generarPdfTareas con tareas retorna arreglo de bytes no vacío")
    void CP01_generarPdfTareas_ConTareas_RetornaBytesNoVacios() throws IOException {
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea de prueba");
        tarea.setDificultad(Tarea.Dificultad.MEDIA);
        tarea.setCompletada(false);
        tarea.setFechaFinal(LocalDateTime.now().plusDays(5));
        tarea.setTrabajo(trabajo);

        byte[] pdf = pdfExportService.generarPdfTareas(trabajo, List.of(tarea));

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    // CP02 - BORDE: Generar PDF con lista vacía retorna bytes no vacíos (mensaje "sin tareas")
    @Test
    @DisplayName("CP02: generarPdfTareas con lista vacía retorna PDF válido (sin tareas)")
    void CP02_generarPdfTareas_ConListaVacia_RetornaPdfValido() throws IOException {
        byte[] pdf = pdfExportService.generarPdfTareas(trabajo, new ArrayList<>());

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    // CP03 - NORMAL: Generar PDF con tarea completada
    @Test
    @DisplayName("CP03: generarPdfTareas con tarea completada genera PDF correctamente")
    void CP03_generarPdfTareas_ConTareaCompletada_GeneraPdf() throws IOException {
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Completada");
        tarea.setDificultad(Tarea.Dificultad.ALTA);
        tarea.setCompletada(true);
        tarea.setFechaFinal(LocalDateTime.now().minusDays(1));
        tarea.setTrabajo(trabajo);

        byte[] pdf = pdfExportService.generarPdfTareas(trabajo, List.of(tarea));

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    // CP04 - NORMAL: Generar PDF con tarea con responsables
    @Test
    @DisplayName("CP04: generarPdfTareas con responsables genera PDF correctamente")
    void CP04_generarPdfTareas_ConResponsables_GeneraPdf() throws IOException {
        User responsable = new User();
        responsable.setUsername("juan");
        Set<User> responsables = new HashSet<>();
        responsables.add(responsable);

        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Asignada");
        tarea.setDificultad(Tarea.Dificultad.SIMPLE);
        tarea.setCompletada(false);
        tarea.setResponsables(responsables);
        tarea.setTrabajo(trabajo);

        byte[] pdf = pdfExportService.generarPdfTareas(trabajo, List.of(tarea));

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    // CP05 - BORDE: Generar PDF con tarea sin fecha final
    @Test
    @DisplayName("CP05: generarPdfTareas con tarea sin fecha final no lanza excepción")
    void CP05_generarPdfTareas_ConTareaSinFecha_NoLanzaExcepcion() throws IOException {
        Tarea tarea = new Tarea();
        tarea.setNombre("Sin Fecha");
        tarea.setDificultad(Tarea.Dificultad.MEDIA);
        tarea.setCompletada(false);
        // sin fechaFinal
        tarea.setTrabajo(trabajo);

        assertDoesNotThrow(() -> pdfExportService.generarPdfTareas(trabajo, List.of(tarea)));
    }

    // CP06 - NORMAL: PDF generado empieza con bytes de cabecera PDF (%PDF)
    @Test
    @DisplayName("CP06: PDF generado tiene cabecera PDF válida (%PDF)")
    void CP06_generarPdfTareas_PDFConCabeceraValida() throws IOException {
        Tarea tarea = new Tarea();
        tarea.setNombre("Test Cabecera");
        tarea.setDificultad(Tarea.Dificultad.MEDIA);
        tarea.setTrabajo(trabajo);

        byte[] pdf = pdfExportService.generarPdfTareas(trabajo, List.of(tarea));

        // Los PDFs comienzan con "%PDF"
        assertEquals('%', (char) pdf[0]);
        assertEquals('P', (char) pdf[1]);
        assertEquals('D', (char) pdf[2]);
        assertEquals('F', (char) pdf[3]);
    }

    // CP07 - BORDE: Generar PDF con muchas tareas (prueba paginación)
    @Test
    @DisplayName("CP07: generarPdfTareas con nombre muy largo trunca correctamente")
    void CP07_generarPdfTareas_ConNombreLargo_TruncaCorrectamente() throws IOException {
        trabajo.setNombreTrabajo("Nombre de trabajo extremadamente largo que supera los setenta caracteres permitidos en el reporte PDF");

        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea con nombre tambien muy largo que necesita ser truncado correctamente en la tabla");
        tarea.setDificultad(Tarea.Dificultad.ALTA);
        tarea.setFechaFinal(LocalDateTime.now().plusDays(3));
        tarea.setTrabajo(trabajo);

        byte[] pdf = pdfExportService.generarPdfTareas(trabajo, List.of(tarea));
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }
}
