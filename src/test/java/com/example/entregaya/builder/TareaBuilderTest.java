package com.example.entregaya.builder;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas manuales de TareaBuilder HU-21 #268

// Tests para TareaBuilder - Patron Builder HU-21 #268

class TareaBuilderTest {

    // CP01 - NORMAL: Se justifica para validar que el builder crea una tarea valida con todos los parametros correctos.
    // Entrada: nombre="Sprint 1", fechas validas
    // Resultados Esperados: Objeto Tarea creado correctamente.
    @Test
    void CP01_Build_ConDatosValidos_RetornaTareaCorrecta() {
        // Arrange
        LocalDateTime inicio = LocalDateTime.of(2026, 5, 1, 10, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 5, 10, 10, 0);
        Trabajo trabajo = new Trabajo();

        // Act
        Tarea tarea = new TareaBuilder()
                .nombre("Sprint 1")
                .descripcion("Sprint inicial")
                .fechaInicio(inicio)
                .fechaFinal(fin)
                .dificultad(Tarea.Dificultad.MEDIA)
                .trabajo(trabajo)
                .build();

        // Assert
        assertNotNull(tarea);
        assertEquals("Sprint 1", tarea.getNombre());
        assertEquals(inicio, tarea.getFechaInicio());
        assertEquals(fin, tarea.getFechaFinal());
    }

    // CP02 - NEGATIVA: Nombre Nulo. Se justifica para prevenir errores de integridad al evitar que se instancien tareas sin nombre.
    // Entrada: conNombre(null)
    // Resultados Esperados: Lanza IllegalStateException.
    @Test
    void CP02_Build_ConNombreNull_LanzaExcepcion() {
        // Arrange
        TareaBuilder builder = new TareaBuilder();

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> builder.nombre(null).build());
    }

    // CP03 - BORDE: Nombre en Blanco. Se justifica para asegurar que nombres compuestos solo por espacios no sean aceptados como validos.
    // Entrada: conNombre("   ")
    // Resultados Esperados: Lanza IllegalStateException.
    @Test
    void CP03_Build_ConNombreEnBlanco_LanzaExcepcion() {
        // Arrange
        TareaBuilder builder = new TareaBuilder();

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> builder.nombre("   ").build());
    }

    // CP04 - NEGATIVA: Cronologia Invalida. Se justifica para proteger la logica de negocio impidiendo que una tarea termine antes de empezar.
    // Entrada: inicio = manana, fin = ayer
    // Resultados Esperados: Lanza IllegalStateException.
    @Test
    void CP04_Build_ConCronologiaInvalida_LanzaExcepcion() {
        // Arrange
        LocalDateTime inicio = LocalDateTime.of(2026, 5, 10, 10, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 5, 1, 10, 0);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () ->
                new TareaBuilder()
                        .nombre("Tarea con fechas cruzadas")
                        .fechaInicio(inicio)
                        .fechaFinal(fin)
                        .build());

        assertTrue(exception.getMessage().contains("fecha"));
    }

    // CP05 - BORDE: Duracion Cero. Se justifica para verificar si el sistema permite hitos o tareas que ocurren en un instante exacto.
    // Entrada: inicio == fin
    // Resultados Esperados: Objeto creado (Duracion 0s).
    @Test
    void CP05_Build_ConDuracionCero_PermiteCreacion() {
        // Arrange
        LocalDateTime instante = LocalDateTime.of(2026, 5, 1, 12, 0);

        // Act
        Tarea tarea = new TareaBuilder()
                .nombre("Hito Instantaneo")
                .fechaInicio(instante)
                .fechaFinal(instante)
                .build();

        // Assert
        assertNotNull(tarea);
        assertEquals(tarea.getFechaInicio(), tarea.getFechaFinal());
    }
}
