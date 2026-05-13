package com.example.entregaya.observer;

import com.example.entregaya.dto.TareaEventoDTO;
import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.NotificacionRepository;
import com.example.entregaya.repository.TareaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Tests para NotificacionObserver - persiste notificaciones al recibir eventos de tarea

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacionObserver - Tests unitarios con Mockito")
class NotificacionObserverTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private TareaRepository tareaRepository;

    @InjectMocks
    private NotificacionObserver notificacionObserver;

    // CP01 - NORMAL: actualizar con tarea que tiene responsables crea notificaciones.
    // Se justifica para verificar el flujo principal: un evento de estado debe generar notificaciones.
    // Entrada: TareaEventoDTO COMPLETADA, tarea con 1 responsable
    // Resultados Esperados: notificacionRepository.save() llamado exactamente 1 vez.
    @Test
    @DisplayName("CP01: actualizar con responsables llama a save para cada responsable")
    void CP01_Actualizar_ConResponsables_CreaNoficacionPorCadaUno() {
        // Arrange
        User responsable = new User();
        responsable.setUsername("responsable1");

        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Test");

        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Test");
        tarea.setTrabajo(trabajo);
        tarea.setResponsables(Set.of(responsable));

        TareaEventoDTO evento = new TareaEventoDTO(
                1L, "Tarea Test", 10L, "Proyecto Test",
                TareaEventoDTO.TipoEvento.COMPLETADA,
                null, "admin", LocalDateTime.now()
        );

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
        when(notificacionRepository.save(any(Notificacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        notificacionObserver.actualizar(evento);

        // Assert
        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    // CP02 - BORDE: actualizar con tarea sin responsables no crea notificaciones.
    // Se justifica para cumplir la regla CA6: si no hay responsables, no se genera ninguna notificación.
    // Entrada: Tarea con responsables vacío
    // Resultados Esperados: notificacionRepository.save() nunca se llama.
    @Test
    @DisplayName("CP02: actualizar con responsables vacíos no llama a save")
    void CP02_Actualizar_SinResponsables_NoCreaNingundaNotificacion() {
        // Arrange
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Sin Responsables");
        tarea.setResponsables(new HashSet<>());

        TareaEventoDTO evento = new TareaEventoDTO(
                2L, "Tarea Sin Responsables", 10L, "Proyecto",
                TareaEventoDTO.TipoEvento.COMPLETADA,
                null, "admin", LocalDateTime.now()
        );

        when(tareaRepository.findById(2L)).thenReturn(Optional.of(tarea));

        // Act
        notificacionObserver.actualizar(evento);

        // Assert
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    // CP03 - NEGATIVA: actualizar con tareaId inexistente no hace nada.
    // Se justifica para confirmar que el observer no falla ante IDs de tarea que no existen.
    // Entrada: tareaRepository.findById retorna Optional.empty()
    // Resultados Esperados: ninguna operación en notificacionRepository.
    @Test
    @DisplayName("CP03: actualizar con tareaId inexistente no genera ninguna operación")
    void CP03_Actualizar_ConTareaIdInexistente_NoHaceNada() {
        // Arrange
        TareaEventoDTO evento = new TareaEventoDTO(
                999L, "Tarea Fantasma", 10L, "Proyecto",
                TareaEventoDTO.TipoEvento.COMPLETADA,
                null, "admin", LocalDateTime.now()
        );

        when(tareaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        notificacionObserver.actualizar(evento);

        // Assert
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    // CP04 - NORMAL: el mensaje de notificación contiene el nombre de la tarea y del trabajo.
    // Se justifica para garantizar que el mensaje es informativo y cumple CA2 del criterio de aceptación.
    // Entrada: evento COMPLETADA con nombreTarea="Tarea A" y nombreTrabajo="Proyecto B"
    // Resultados Esperados: El mensaje guardado contiene ambos nombres.
    @Test
    @DisplayName("CP04: el mensaje de notificación incluye nombre de tarea y de trabajo")
    void CP04_Actualizar_MensajeNotificacion_ContieneNombreTareaYTrabajo() {
        // Arrange
        User responsable = new User();
        responsable.setUsername("dev");

        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea A");
        tarea.setResponsables(Set.of(responsable));

        TareaEventoDTO evento = new TareaEventoDTO(
                5L, "Tarea A", 20L, "Proyecto B",
                TareaEventoDTO.TipoEvento.COMPLETADA,
                null, "lider", LocalDateTime.now()
        );

        when(tareaRepository.findById(5L)).thenReturn(Optional.of(tarea));

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        when(notificacionRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        notificacionObserver.actualizar(evento);

        // Assert
        String mensaje = captor.getValue().getMensaje();
        assertTrue(mensaje.contains("Tarea A"));
        assertTrue(mensaje.contains("Proyecto B"));
    }

    // CP05 - NORMAL: se crea una notificación por cada responsable asignado.
    // Se justifica para asegurar que todos los responsables reciben el aviso del cambio de estado.
    // Entrada: Tarea con 2 responsables
    // Resultados Esperados: notificacionRepository.save() llamado exactamente 2 veces.
    @Test
    @DisplayName("CP05: se crea exactamente una notificación por cada responsable")
    void CP05_Actualizar_ConDosResponsables_GuardaDosNotificaciones() {
        // Arrange
        User responsable1 = new User();
        responsable1.setUsername("dev1");

        User responsable2 = new User();
        responsable2.setUsername("dev2");

        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea Compartida");
        tarea.setResponsables(Set.of(responsable1, responsable2));

        TareaEventoDTO evento = new TareaEventoDTO(
                3L, "Tarea Compartida", 10L, "Proyecto",
                TareaEventoDTO.TipoEvento.COMPLETADA,
                null, "admin", LocalDateTime.now()
        );

        when(tareaRepository.findById(3L)).thenReturn(Optional.of(tarea));
        when(notificacionRepository.save(any(Notificacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        notificacionObserver.actualizar(evento);

        // Assert
        verify(notificacionRepository, times(2)).save(any(Notificacion.class));
    }
}
