package com.example.entregaya.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Tests para TareaEventoDTO - DTO de eventos de cambios en tareas utilizado por el patrón Observer

@DisplayName("TareaEventoDTO - Tests unitarios")
class TareaEventoDTOTest {

    // CP01 - NORMAL: nuevoEstado retorna "COMPLETADA" cuando tipoEvento es COMPLETADA.
    // Se justifica para verificar el método de compatibilidad que indica el nuevo estado de la tarea.
    // Entrada: tipoEvento = COMPLETADA
    // Resultados Esperados: nuevoEstado() == "COMPLETADA".
    @Test
    @DisplayName("CP01: nuevoEstado retorna COMPLETADA cuando tipoEvento es COMPLETADA")
    void CP01_NuevoEstado_ConTipoEventoCompletada_RetornaCompletada() {
        // Arrange
        TareaEventoDTO evento = new TareaEventoDTO(
                1L, "Tarea Test", 10L, "Proyecto Test",
                TareaEventoDTO.TipoEvento.COMPLETADA,
                null, "usuario1", LocalDateTime.now()
        );

        // Act
        String estado = evento.nuevoEstado();

        // Assert
        assertEquals("COMPLETADA", estado);
    }

    // CP02 - NORMAL: nuevoEstado retorna "PENDIENTE" cuando tipoEvento es INCOMPLETADA.
    // Se justifica para confirmar que al revertir una tarea completada el estado refleja "PENDIENTE".
    // Entrada: tipoEvento = INCOMPLETADA
    // Resultados Esperados: nuevoEstado() == "PENDIENTE".
    @Test
    @DisplayName("CP02: nuevoEstado retorna PENDIENTE cuando tipoEvento es INCOMPLETADA")
    void CP02_NuevoEstado_ConTipoEventoIncompletada_RetornaPendiente() {
        // Arrange
        TareaEventoDTO evento = new TareaEventoDTO(
                1L, "Tarea Test", 10L, "Proyecto Test",
                TareaEventoDTO.TipoEvento.INCOMPLETADA,
                null, "usuario1", LocalDateTime.now()
        );

        // Act
        String estado = evento.nuevoEstado();

        // Assert
        assertEquals("PENDIENTE", estado);
    }

    // CP03 - NORMAL: realizadoPor retorna el mismo valor que usuarioAccion.
    // Se justifica para verificar el método de compatibilidad que expone quién ejecutó la acción.
    // Entrada: usuarioAccion = "gestor"
    // Resultados Esperados: realizadoPor() == "gestor".
    @Test
    @DisplayName("CP03: realizadoPor retorna el mismo valor que usuarioAccion")
    void CP03_RealizadoPor_RetornaUsuarioAccion() {
        // Arrange
        TareaEventoDTO evento = new TareaEventoDTO(
                2L, "Tarea B", 5L, "Proyecto B",
                TareaEventoDTO.TipoEvento.EDITADA,
                "Cambio de nombre", "gestor", LocalDateTime.now()
        );

        // Act
        String realizadoPor = evento.realizadoPor();

        // Assert
        assertEquals("gestor", realizadoPor);
    }

    // CP04 - NORMAL: getDescripcion del enum TipoEvento retorna la descripción correcta.
    // Se justifica para asegurar que cada evento tiene una descripción legible para el usuario.
    // Entrada: TipoEvento.CREACION
    // Resultados Esperados: getDescripcion() == "Tarea creada".
    @Test
    @DisplayName("CP04: TipoEvento.CREACION tiene descripción 'Tarea creada'")
    void CP04_TipoEvento_Creacion_TieneDescripcionCorrecta() {
        // Arrange
        TareaEventoDTO.TipoEvento tipo = TareaEventoDTO.TipoEvento.CREACION;

        // Act
        String descripcion = tipo.getDescripcion();

        // Assert
        assertEquals("Tarea creada", descripcion);
    }

    // CP05 - NORMAL: Todos los campos del record son accesibles correctamente.
    // Se justifica para garantizar que el record expone todos sus componentes sin pérdida de datos.
    // Entrada: record completo con tareaId, nombreTarea, trabajoId, nombreTrabajo, detalles, fechaEvento
    // Resultados Esperados: Cada campo retorna el valor pasado en el constructor.
    @Test
    @DisplayName("CP05: Todos los campos del record son accesibles sin pérdida de datos")
    void CP05_CamposDelRecord_TodosAccesibles_RetornanValoresCorrectos() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.of(2026, 4, 20, 10, 0);

        // Act
        TareaEventoDTO evento = new TareaEventoDTO(
                99L, "Tarea Importante", 7L, "Proyecto Clave",
                TareaEventoDTO.TipoEvento.EDITADA,
                "Descripción actualizada", "editor1", fecha
        );

        // Assert
        assertEquals(99L, evento.tareaId());
        assertEquals("Tarea Importante", evento.nombreTarea());
        assertEquals(7L, evento.trabajoId());
        assertEquals("Proyecto Clave", evento.nombreTrabajo());
        assertEquals(TareaEventoDTO.TipoEvento.EDITADA, evento.tipoEvento());
        assertEquals("Descripción actualizada", evento.detalles());
        assertEquals("editor1", evento.usuarioAccion());
        assertEquals(fecha, evento.fechaEvento());
    }
}
