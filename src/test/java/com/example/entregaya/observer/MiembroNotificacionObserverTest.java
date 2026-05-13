package com.example.entregaya.observer;

import com.example.entregaya.dto.TrabajoEventoDTO;
import com.example.entregaya.model.ColaboradorTrabajo;
import com.example.entregaya.model.HistorialEvento;
import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.HistorialEventoRepository;
import com.example.entregaya.repository.NotificacionRepository;
import com.example.entregaya.repository.TrabajoRepository;
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

// Tests para MiembroNotificacionObserver - notifica a miembros y registra eventos en historial

@ExtendWith(MockitoExtension.class)
@DisplayName("MiembroNotificacionObserver - Tests unitarios con Mockito")
class MiembroNotificacionObserverTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private TrabajoRepository trabajoRepository;

    @Mock
    private HistorialEventoRepository historialEventoRepository;

    @InjectMocks
    private MiembroNotificacionObserver miembroNotificacionObserver;

    // CP01 - NORMAL: actualizar INGRESO crea notificaciones solo a los otros miembros.
    // Se justifica para verificar que el usuario afectado no recibe su propia notificación de ingreso.
    // Entrada: Trabajo con 2 colaboradores, evento INGRESO del primero
    // Resultados Esperados: save() de notificacion llamado solo 1 vez (al otro colaborador).
    @Test
    @DisplayName("CP01: actualizar INGRESO notifica solo a los miembros distintos al afectado")
    void CP01_Actualizar_Ingreso_NotificaSoloAOtrosMiembros() {
        // Arrange
        User miembroExistente = new User();
        miembroExistente.setUsername("miembroAntiguo");

        User nuevoMiembro = new User();
        nuevoMiembro.setUsername("nuevoMiembro");

        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Equipo");

        ColaboradorTrabajo colab1 = new ColaboradorTrabajo();
        colab1.setUser(miembroExistente);
        colab1.setRol(ColaboradorTrabajo.Rol.EDITOR);

        ColaboradorTrabajo colab2 = new ColaboradorTrabajo();
        colab2.setUser(nuevoMiembro);
        colab2.setRol(ColaboradorTrabajo.Rol.COLABORADOR);

        trabajo.getColaboradores().add(colab1);
        trabajo.getColaboradores().add(colab2);

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                1L, "Proyecto Equipo",
                TrabajoEventoDTO.TipoEvento.INGRESO,
                "nuevoMiembro", "admin", LocalDateTime.now()
        );

        when(trabajoRepository.findById(1L)).thenReturn(Optional.of(trabajo));
        when(notificacionRepository.save(any(Notificacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        miembroNotificacionObserver.actualizar(evento);

        // Assert
        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    // CP02 - NORMAL: actualizar SALIDA construye un mensaje con "abandonó el".
    // Se justifica para confirmar que el tipo de evento determina el texto del mensaje de notificación.
    // Entrada: evento SALIDA con afectadoUsername = "exMiembro"
    // Resultados Esperados: El mensaje guardado contiene "abandonó el".
    @Test
    @DisplayName("CP02: actualizar SALIDA produce mensaje con 'abandonó el'")
    void CP02_Actualizar_Salida_MensajeContieneAbandonoEl() {
        // Arrange
        User miembroRestante = new User();
        miembroRestante.setUsername("miembroRestante");

        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Alpha");

        ColaboradorTrabajo colab = new ColaboradorTrabajo();
        colab.setUser(miembroRestante);
        colab.setRol(ColaboradorTrabajo.Rol.LIDER);

        trabajo.getColaboradores().add(colab);

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                2L, "Proyecto Alpha",
                TrabajoEventoDTO.TipoEvento.SALIDA,
                "exMiembro", "admin", LocalDateTime.now()
        );

        when(trabajoRepository.findById(2L)).thenReturn(Optional.of(trabajo));

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        when(notificacionRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        miembroNotificacionObserver.actualizar(evento);

        // Assert
        assertTrue(captor.getValue().getMensaje().contains("abandonó el"));
    }

    // CP03 - NEGATIVA: actualizar con trabajoId inexistente no hace nada.
    // Se justifica para asegurar que el observer es resiliente ante trabajos eliminados o con ID inválido.
    // Entrada: trabajoRepository.findById retorna Optional.empty()
    // Resultados Esperados: ni notificacion ni historial se persisten.
    @Test
    @DisplayName("CP03: actualizar con trabajoId inexistente no persiste nada")
    void CP03_Actualizar_ConTrabajoInexistente_NoHaceNada() {
        // Arrange
        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                999L, "Proyecto Fantasma",
                TrabajoEventoDTO.TipoEvento.INGRESO,
                "usuario", "admin", LocalDateTime.now()
        );

        when(trabajoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        miembroNotificacionObserver.actualizar(evento);

        // Assert
        verify(notificacionRepository, never()).save(any(Notificacion.class));
        verify(historialEventoRepository, never()).save(any(HistorialEvento.class));
    }

    // CP04 - NORMAL: actualizar registra el evento en el historial del trabajo.
    // Se justifica para garantizar la trazabilidad de todos los cambios de membresía.
    // Entrada: evento INGRESO con trabajo existente
    // Resultados Esperados: historialEventoRepository.save() llamado exactamente 1 vez.
    @Test
    @DisplayName("CP04: actualizar persiste exactamente un evento en el historial")
    void CP04_Actualizar_ConTrabajoExistente_PersistUnEventoEnHistorial() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Historia");

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                3L, "Proyecto Historia",
                TrabajoEventoDTO.TipoEvento.INGRESO,
                "nuevoIntegrante", "lider", LocalDateTime.now()
        );

        when(trabajoRepository.findById(3L)).thenReturn(Optional.of(trabajo));
        when(historialEventoRepository.save(any(HistorialEvento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        miembroNotificacionObserver.actualizar(evento);

        // Assert
        verify(historialEventoRepository, times(1)).save(any(HistorialEvento.class));
    }

    // CP05 - NORMAL: el evento de historial para INGRESO usa TipoEvento INGRESO_MIEMBRO.
    // Se justifica para comprobar que el tipo de evento del historial es coherente con el tipo del DTO.
    // Entrada: evento TrabajoEventoDTO.TipoEvento.INGRESO
    // Resultados Esperados: El HistorialEvento guardado tiene tipoEvento INGRESO_MIEMBRO.
    @Test
    @DisplayName("CP05: evento de historial para INGRESO tiene tipo INGRESO_MIEMBRO")
    void CP05_Actualizar_Ingreso_HistorialUsaTipoIngresoMiembro() {
        // Arrange
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Historial");

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                4L, "Proyecto Historial",
                TrabajoEventoDTO.TipoEvento.INGRESO,
                "entrante", "admin", LocalDateTime.now()
        );

        when(trabajoRepository.findById(4L)).thenReturn(Optional.of(trabajo));

        ArgumentCaptor<HistorialEvento> captor = ArgumentCaptor.forClass(HistorialEvento.class);
        when(historialEventoRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        miembroNotificacionObserver.actualizar(evento);

        // Assert
        assertEquals(HistorialEvento.TipoEvento.INGRESO_MIEMBRO, captor.getValue().getTipoEvento());
    }
}
