package com.example.entregaya.observer;

import com.example.entregaya.dto.TareaEventoDTO;
import com.example.entregaya.model.HistorialEvento;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.repository.HistorialEventoRepository;
import com.example.entregaya.repository.TareaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Tests para TareaHistorialObserver - registra eventos de tareas en el historial del trabajo

@ExtendWith(MockitoExtension.class)
@DisplayName("TareaHistorialObserver - Tests unitarios con Mockito")
class TareaHistorialObserverTest {

    @Mock
    private HistorialEventoRepository historialEventoRepository;

    @Mock
    private TareaRepository tareaRepository;

    @InjectMocks
    private TareaHistorialObserver tareaHistorialObserver;

    // CP01 - NORMAL: actualizar con evento CREACION genera descripción correcta.
    // Se justifica para verificar que el historial refleja con exactitud cuándo se crea una tarea.
    // Entrada: TareaEventoDTO con TipoEvento.CREACION y nombreTarea = "Nueva Tarea"
    // Resultados Esperados: El HistorialEvento guardado contiene "fue creada" en la descripción.
    @Test
    @DisplayName("CP01: evento CREACION genera descripción con 'fue creada'")
    void CP01_Actualizar_Creacion_DescripcionContieneFueCreada() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto");

        Tarea tarea = new Tarea();
        tarea.setNombre("Nueva Tarea");
        tarea.setTrabajo(trabajo);

        TareaEventoDTO evento = new TareaEventoDTO(
                1L, "Nueva Tarea", 10L, "Proyecto",
                TareaEventoDTO.TipoEvento.CREACION,
                null, "creador", LocalDateTime.now()
        );

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
        ArgumentCaptor<HistorialEvento> captor = ArgumentCaptor.forClass(HistorialEvento.class);
        when(historialEventoRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        tareaHistorialObserver.actualizar(evento);

        // Assert
        assertTrue(captor.getValue().getDescripcion().contains("fue creada"));
        assertEquals(HistorialEvento.TipoEvento.CREACION_TAREA, captor.getValue().getTipoEvento());
    }

    // CP02 - NORMAL: actualizar con evento COMPLETADA mapea tipo CAMBIO_ESTADO_TAREA.
    // Se justifica para confirmar que el cambio de estado a completado se archiva con el tipo correcto.
    // Entrada: TareaEventoDTO con TipoEvento.COMPLETADA
    // Resultados Esperados: HistorialEvento guardado tiene tipoEvento CAMBIO_ESTADO_TAREA.
    @Test
    @DisplayName("CP02: evento COMPLETADA se registra con tipo CAMBIO_ESTADO_TAREA")
    void CP02_Actualizar_Completada_MapaTipoEventoCambioEstado() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Completada");
        tarea.setTrabajo(trabajo);

        TareaEventoDTO evento = new TareaEventoDTO(
                2L, "Tarea Completada", 10L, "Proyecto",
                TareaEventoDTO.TipoEvento.COMPLETADA,
                null, "usuario", LocalDateTime.now()
        );

        when(tareaRepository.findById(2L)).thenReturn(Optional.of(tarea));
        ArgumentCaptor<HistorialEvento> captor = ArgumentCaptor.forClass(HistorialEvento.class);
        when(historialEventoRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        tareaHistorialObserver.actualizar(evento);

        // Assert
        assertEquals(HistorialEvento.TipoEvento.CAMBIO_ESTADO_TAREA, captor.getValue().getTipoEvento());
        assertTrue(captor.getValue().getDescripcion().contains("completada"));
    }

    // CP03 - NORMAL: actualizar con evento INCOMPLETADA genera descripción con "incompleta".
    // Se justifica para verificar que revertir el estado de completado queda registrado correctamente.
    // Entrada: TareaEventoDTO con TipoEvento.INCOMPLETADA
    // Resultados Esperados: La descripción del HistorialEvento contiene "incompleta".
    @Test
    @DisplayName("CP03: evento INCOMPLETADA genera descripción con 'incompleta'")
    void CP03_Actualizar_Incompletada_DescripcionContieneIncompleta() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Revertida");
        tarea.setTrabajo(trabajo);

        TareaEventoDTO evento = new TareaEventoDTO(
                3L, "Tarea Revertida", 10L, "Proyecto",
                TareaEventoDTO.TipoEvento.INCOMPLETADA,
                null, "usuario", LocalDateTime.now()
        );

        when(tareaRepository.findById(3L)).thenReturn(Optional.of(tarea));
        ArgumentCaptor<HistorialEvento> captor = ArgumentCaptor.forClass(HistorialEvento.class);
        when(historialEventoRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        tareaHistorialObserver.actualizar(evento);

        // Assert
        assertTrue(captor.getValue().getDescripcion().contains("incompleta"));
    }

    // CP04 - NORMAL: actualizar con evento EDITADA genera descripción con "fue editada".
    // Se justifica para asegurar que las modificaciones de una tarea quedan trazadas en el historial.
    // Entrada: TareaEventoDTO con TipoEvento.EDITADA
    // Resultados Esperados: La descripción del HistorialEvento contiene "fue editada".
    @Test
    @DisplayName("CP04: evento EDITADA genera descripción con 'fue editada'")
    void CP04_Actualizar_Editada_DescripcionContieneFueEditada() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Editada");
        tarea.setTrabajo(trabajo);

        TareaEventoDTO evento = new TareaEventoDTO(
                4L, "Tarea Editada", 10L, "Proyecto",
                TareaEventoDTO.TipoEvento.EDITADA,
                "Cambio de descripción", "editor", LocalDateTime.now()
        );

        when(tareaRepository.findById(4L)).thenReturn(Optional.of(tarea));
        ArgumentCaptor<HistorialEvento> captor = ArgumentCaptor.forClass(HistorialEvento.class);
        when(historialEventoRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        tareaHistorialObserver.actualizar(evento);

        // Assert
        assertTrue(captor.getValue().getDescripcion().contains("fue editada"));
    }

    // CP05 - NEGATIVA: actualizar con tareaId inexistente no persiste historial.
    // Se justifica para confirmar que el observer es resiliente ante eventos con IDs inválidos.
    // Entrada: tareaRepository.findById retorna Optional.empty()
    // Resultados Esperados: historialEventoRepository.save() nunca se invoca.
    @Test
    @DisplayName("CP05: actualizar con tareaId inexistente no persiste ningún evento en historial")
    void CP05_Actualizar_ConTareaIdInexistente_NoPersistHistorial() {
        // Arrange
        TareaEventoDTO evento = new TareaEventoDTO(
                999L, "Tarea Fantasma", 10L, "Proyecto",
                TareaEventoDTO.TipoEvento.CREACION,
                null, "admin", LocalDateTime.now()
        );

        when(tareaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        tareaHistorialObserver.actualizar(evento);

        // Assert
        verify(historialEventoRepository, never()).save(any(HistorialEvento.class));
    }
}
