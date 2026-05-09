package com.example.entregaya.dto;

import com.example.entregaya.model.HistorialEvento;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Tests para HistorialEventoDTO - conversión desde la entidad HistorialEvento

@DisplayName("HistorialEventoDTO - Tests unitarios")
class HistorialEventoDTOTest {

    // CP01 - NORMAL: fromEntity con tarea asociada.
    // Se justifica para verificar que todos los campos del evento se mapean correctamente al DTO.
    // Entrada: HistorialEvento con tarea, tipoEvento, descripcion, usuarioAccion y fechaEvento
    // Resultados Esperados: El DTO refleja exactamente los datos de la entidad.
    @Test
    @DisplayName("CP01: fromEntity con tarea asociada mapea todos los campos correctamente")
    void CP01_FromEntity_ConTareaAsociada_MapeatodosLosCampos() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Test");

        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Ejemplo");

        LocalDateTime fecha = LocalDateTime.of(2026, 5, 10, 9, 0);
        HistorialEvento evento = new HistorialEvento(
                trabajo,
                HistorialEvento.TipoEvento.CREACION_TAREA,
                "Tarea creada",
                "Detalles adicionales",
                "admin",
                fecha,
                tarea
        );

        // Act
        HistorialEventoDTO dto = HistorialEventoDTO.fromEntity(evento);

        // Assert
        assertEquals("CREACION_TAREA", dto.tipoEvento());
        assertEquals("Tarea creada", dto.descripcion());
        assertEquals("Detalles adicionales", dto.detalles());
        assertEquals("admin", dto.usuarioAccion());
        assertEquals(fecha, dto.fechaEvento());
    }

    // CP02 - BORDE: fromEntity con tarea null.
    // Se justifica para comprobar que el DTO gestiona eventos sin tarea sin lanzar excepción.
    // Entrada: HistorialEvento con tarea = null
    // Resultados Esperados: tareaId y nombreTarea del DTO son null.
    @Test
    @DisplayName("CP02: fromEntity con tarea null deja tareaId y nombreTarea en null")
    void CP02_FromEntity_ConTareaNula_TareaIdYNombreNulos() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Sin Tarea");

        HistorialEvento evento = new HistorialEvento(
                trabajo,
                HistorialEvento.TipoEvento.INGRESO_MIEMBRO,
                "Miembro ingresó",
                "testuser",
                LocalDateTime.now()
        );

        // Act
        HistorialEventoDTO dto = HistorialEventoDTO.fromEntity(evento);

        // Assert
        assertNull(dto.tareaId());
        assertNull(dto.nombreTarea());
    }

    // CP03 - NORMAL: tipoEvento se mapea como el nombre del enum.
    // Se justifica para garantizar que la serialización del tipo de evento usa el nombre exacto del enum.
    // Entrada: HistorialEvento con tipoEvento CAMBIO_ESTADO_TAREA
    // Resultados Esperados: dto.tipoEvento() == "CAMBIO_ESTADO_TAREA".
    @Test
    @DisplayName("CP03: tipoEvento se mapea usando el nombre del enum")
    void CP03_FromEntity_TipoEvento_MapesaComoNombreEnum() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        HistorialEvento evento = new HistorialEvento(
                trabajo,
                HistorialEvento.TipoEvento.CAMBIO_ESTADO_TAREA,
                "Estado cambiado",
                "usuario1",
                LocalDateTime.now()
        );

        // Act
        HistorialEventoDTO dto = HistorialEventoDTO.fromEntity(evento);

        // Assert
        assertEquals("CAMBIO_ESTADO_TAREA", dto.tipoEvento());
    }

    // CP04 - NORMAL: nombreTarea del DTO es el nombre de la tarea asociada.
    // Se justifica para confirmar que el DTO extrae correctamente el nombre de la tarea.
    // Entrada: Tarea con nombre "Mi Tarea Importante"
    // Resultados Esperados: dto.nombreTarea() == "Mi Tarea Importante".
    @Test
    @DisplayName("CP04: nombreTarea del DTO refleja el nombre de la tarea asociada")
    void CP04_FromEntity_NombreTarea_RefleleNombreDeLaTarea() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        Tarea tarea = new Tarea();
        tarea.setNombre("Mi Tarea Importante");

        HistorialEvento evento = new HistorialEvento(
                trabajo,
                HistorialEvento.TipoEvento.CREACION_TAREA,
                "Tarea creada",
                "detalles",
                "usuario",
                LocalDateTime.now(),
                tarea
        );

        // Act
        HistorialEventoDTO dto = HistorialEventoDTO.fromEntity(evento);

        // Assert
        assertEquals("Mi Tarea Importante", dto.nombreTarea());
    }

    // CP05 - NORMAL: fechaEvento se preserva sin modificaciones.
    // Se justifica para asegurar que no se altera la marca de tiempo del evento al crear el DTO.
    // Entrada: fechaEvento con fecha específica
    // Resultados Esperados: dto.fechaEvento() idéntico al original.
    @Test
    @DisplayName("CP05: fechaEvento se preserva intacta en el DTO")
    void CP05_FromEntity_FechaEvento_SePreservaSinCambios() {
        // Arrange
        LocalDateTime fechaEspecifica = LocalDateTime.of(2026, 3, 15, 14, 30, 0);
        Trabajo trabajo = new Trabajo();
        HistorialEvento evento = new HistorialEvento(
                trabajo,
                HistorialEvento.TipoEvento.EDICION_TRABAJO,
                "Trabajo editado",
                "editor",
                fechaEspecifica
        );

        // Act
        HistorialEventoDTO dto = HistorialEventoDTO.fromEntity(evento);

        // Assert
        assertEquals(fechaEspecifica, dto.fechaEvento());
    }
}
