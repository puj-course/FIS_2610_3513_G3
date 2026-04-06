package com.example.entregaya.builder;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas manuales de TareaBuilder HU-21 #268

class TareaBuilderTest {

    // Caso 1: nombre vacío lanza IllegalStateException
    @Test
    void build_debeRechazar_nombreVacio() {
        TareaBuilder builder = new TareaBuilder()
                .nombre("")
                .descripcion("Sin nombre");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class, builder::build);

        assertTrue(ex.getMessage().contains("nombre"),
                "El mensaje debe mencionar 'nombre'");
    }

    // Caso 1b: nombre nulo tambien lanza IllegalStateException
    @Test
    void build_debeRechazar_nombreNulo() {
        TareaBuilder builder = new TareaBuilder()
                .descripcion("Sin nombre");

        assertThrows(IllegalStateException.class, builder::build);
    }

    // Caso 2: fechaFinal anterior a fechaInicio lanza IllegalStateException
    @Test
    void build_debeRechazar_fechaFinalAnteriorAInicio() {
        LocalDateTime inicio = LocalDateTime.of(2025, 6, 10, 9, 0);
        LocalDateTime finalAnterior = LocalDateTime.of(2025, 6, 5, 9, 0);

        TareaBuilder builder = new TareaBuilder()
                .nombre("Tarea con fechas cruzadas")
                .fechaInicio(inicio)
                .fechaFinal(finalAnterior);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class, builder::build);

        assertTrue(ex.getMessage().contains("fecha"),
                "El mensaje debe mencionar 'fecha'");
    }

    // Caso 3: datos validos completos retorna Tarea correctamente configurada
    @Test
    void build_debeRetornar_tareaValida() {
        LocalDateTime inicio = LocalDateTime.of(2025, 6, 1, 9, 0);
        LocalDateTime fin    = LocalDateTime.of(2025, 6, 15, 18, 0);
        Trabajo trabajo = new Trabajo();

        Tarea tarea = new TareaBuilder()
                .nombre("Diseño UI")
                .descripcion("Mockups en Figma")
                .fechaInicio(inicio)
                .fechaFinal(fin)
                .dificultad(Tarea.Dificultad.ALTA)
                .trabajo(trabajo)
                .build();

        assertNotNull(tarea);
        assertEquals("Diseño UI", tarea.getNombre());
        assertEquals("Mockups en Figma", tarea.getDescripcion());
        assertEquals(inicio, tarea.getFechaInicio());
        assertEquals(fin, tarea.getFechaFinal());
        assertEquals(Tarea.Dificultad.ALTA, tarea.getDificultad());
        assertSame(trabajo, tarea.getTrabajo());
        assertNotNull(tarea.getResponsables());
    }

    // Caso 3b: sin fechas no lanza excepcion son opcionales
    @Test
    void build_debeAceptar_sinFechas() {
        Tarea tarea = new TareaBuilder()
                .nombre("Tarea sin fechas")
                .build();

        assertNotNull(tarea);
        assertNull(tarea.getFechaInicio());
        assertNull(tarea.getFechaFinal());
    }
}