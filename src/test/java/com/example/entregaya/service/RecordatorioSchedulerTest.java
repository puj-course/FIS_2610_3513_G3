package com.example.entregaya.service;

import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.NotificacionRepository;
import com.example.entregaya.repository.TareaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecordatorioScheduler - Tests unitarios")
class RecordatorioSchedulerTest {

    @Mock
    private TareaRepository tareaRepository;

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private TwilioSmsService smsService;

    @InjectMocks
    private RecordatorioScheduler scheduler;

    private User userSinPhone;
    private User userConPhone;
    private Tarea tarea;

    @BeforeEach
    void setUp() {
        userSinPhone = new User(1L, "alice", "pass");
        userSinPhone.setPhoneNumber(null);

        userConPhone = new User(2L, "bob", "pass");
        userConPhone.setPhoneNumber("+573001234567");

        tarea = new Tarea();
        tarea.setId(1L);
        tarea.setNombre("Tarea de prueba");
        tarea.setFechaFinal(LocalDateTime.now().plusHours(12));
        tarea.setRecordatorioEnviado(false);
    }

    // CP01 - NORMAL: sin tareas próximas no hace nada
    @Test
    @DisplayName("CP01: enviarRecordatoriosVencimiento sin tareas no crea notificaciones")
    void CP01_enviarRecordatorios_SinTareas_NoCreaNada() {
        when(tareaRepository.findTareasProximasAVencer(any(), any()))
                .thenReturn(Collections.emptyList());

        scheduler.enviarRecordatoriosVencimiento();

        verify(notificacionRepository, never()).save(any());
        verify(smsService, never()).enviarSms(any(), any());
    }

    // CP02 - NORMAL: tarea con responsable sin teléfono solo guarda notificación interna
    @Test
    @DisplayName("CP02: responsable sin phoneNumber recibe notificación interna pero no SMS")
    void CP02_enviarRecordatorios_ResponsableSinPhone_SoloNotificacionInterna() {
        Set<User> responsables = new HashSet<>();
        responsables.add(userSinPhone);
        tarea.setResponsables(responsables);

        when(tareaRepository.findTareasProximasAVencer(any(), any()))
                .thenReturn(List.of(tarea));

        scheduler.enviarRecordatoriosVencimiento();

        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
        verify(smsService, never()).enviarSms(any(), any());
        verify(tareaRepository, times(1)).save(tarea);
        assertTrue(tarea.isRecordatorioEnviado());
    }

    // CP03 - NORMAL: responsable con teléfono recibe notificación interna Y SMS
    @Test
    @DisplayName("CP03: responsable con phoneNumber recibe notificación interna y SMS")
    void CP03_enviarRecordatorios_ResponsableConPhone_EnviaAmbas() {
        Set<User> responsables = new HashSet<>();
        responsables.add(userConPhone);
        tarea.setResponsables(responsables);

        when(tareaRepository.findTareasProximasAVencer(any(), any()))
                .thenReturn(List.of(tarea));

        scheduler.enviarRecordatoriosVencimiento();

        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
        verify(smsService, times(1)).enviarSms(eq("+573001234567"), anyString());
        assertTrue(tarea.isRecordatorioEnviado());
    }

    // CP04 - NORMAL: mensaje de notificación contiene el nombre de la tarea
    @Test
    @DisplayName("CP04: la notificación guardada contiene el nombre de la tarea")
    void CP04_enviarRecordatorios_MensajeContieneNombreTarea() {
        Set<User> responsables = new HashSet<>();
        responsables.add(userSinPhone);
        tarea.setResponsables(responsables);

        when(tareaRepository.findTareasProximasAVencer(any(), any()))
                .thenReturn(List.of(tarea));

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        scheduler.enviarRecordatoriosVencimiento();

        verify(notificacionRepository).save(captor.capture());
        String mensaje = captor.getValue().getMensaje();
        assertTrue(mensaje.contains("Tarea de prueba"),
                "El mensaje debe contener el nombre de la tarea");
    }

    // CP05 - NORMAL: múltiples responsables generan múltiples notificaciones
    @Test
    @DisplayName("CP05: tarea con varios responsables genera una notificación por cada uno")
    void CP05_enviarRecordatorios_VariosResponsables_GeneraMultiplesNotificaciones() {
        Set<User> responsables = new HashSet<>();
        responsables.add(userSinPhone);
        responsables.add(userConPhone);
        tarea.setResponsables(responsables);

        when(tareaRepository.findTareasProximasAVencer(any(), any()))
                .thenReturn(List.of(tarea));

        scheduler.enviarRecordatoriosVencimiento();

        verify(notificacionRepository, times(2)).save(any(Notificacion.class));
        verify(smsService, times(1)).enviarSms(anyString(), anyString());
    }

    // CP06 - NORMAL: múltiples tareas se procesan todas
    @Test
    @DisplayName("CP06: múltiples tareas con responsables son procesadas correctamente")
    void CP06_enviarRecordatorios_MultiplesTareas_ProcesaTodasLasTareas() {
        Tarea tarea2 = new Tarea();
        tarea2.setId(2L);
        tarea2.setNombre("Tarea 2");
        tarea2.setRecordatorioEnviado(false);

        Set<User> responsables = new HashSet<>();
        responsables.add(userSinPhone);
        tarea.setResponsables(responsables);
        tarea2.setResponsables(responsables);

        when(tareaRepository.findTareasProximasAVencer(any(), any()))
                .thenReturn(List.of(tarea, tarea2));

        scheduler.enviarRecordatoriosVencimiento();

        verify(notificacionRepository, times(2)).save(any());
        verify(tareaRepository, times(2)).save(any());
        assertTrue(tarea.isRecordatorioEnviado());
        assertTrue(tarea2.isRecordatorioEnviado());
    }

    // CP07 - BORDE: tarea sin responsables no genera notificaciones
    @Test
    @DisplayName("CP07: tarea sin responsables no genera ninguna notificación")
    void CP07_enviarRecordatorios_SinResponsables_NoGeneraNotificaciones() {
        tarea.setResponsables(new HashSet<>());

        when(tareaRepository.findTareasProximasAVencer(any(), any()))
                .thenReturn(List.of(tarea));

        scheduler.enviarRecordatoriosVencimiento();

        verify(notificacionRepository, never()).save(any());
        verify(smsService, never()).enviarSms(any(), any());
        assertTrue(tarea.isRecordatorioEnviado());
    }

    // CP08 - BORDE: phoneNumber en blanco no envía SMS
    @Test
    @DisplayName("CP08: phoneNumber en blanco no envía SMS")
    void CP08_enviarRecordatorios_PhoneEnBlanco_NoEnviaSms() {
        userConPhone.setPhoneNumber("   "); // blank, not null
        Set<User> responsables = new HashSet<>();
        responsables.add(userConPhone);
        tarea.setResponsables(responsables);

        when(tareaRepository.findTareasProximasAVencer(any(), any()))
                .thenReturn(List.of(tarea));

        scheduler.enviarRecordatoriosVencimiento();

        verify(notificacionRepository, times(1)).save(any());
        verify(smsService, never()).enviarSms(any(), any());
    }
}