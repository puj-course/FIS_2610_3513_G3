package com.example.entregaya.observer;

import com.example.entregaya.dto.TrabajoEventoDTO;
import com.example.entregaya.model.ColaboradorTrabajo;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.TrabajoRepository;
import com.example.entregaya.service.TwilioSmsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SmsMiembroObserver - Tests unitarios")
class SmsMiembroObserverTest {

    @Mock
    private TwilioSmsService smsService;

    @Mock
    private TrabajoRepository trabajoRepository;

    @InjectMocks
    private SmsMiembroObserver observer;

    // ── Helpers ────────────────────────────────────────────────────────────

    private User crearUsuario(String username, String phoneNumber) {
        User user = new User();
        user.setUsername(username);
        user.setPhoneNumber(phoneNumber);
        return user;
    }

    private ColaboradorTrabajo crearColaborador(User user, ColaboradorTrabajo.Rol rol) {
        ColaboradorTrabajo colab = new ColaboradorTrabajo();
        colab.setUser(user);
        colab.setRol(rol);
        return colab;
    }

    private Trabajo crearTrabajo(String nombre, ColaboradorTrabajo... colaboradores) {
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo(nombre);
        for (ColaboradorTrabajo c : colaboradores) {
            trabajo.getColaboradores().add(c);
        }
        return trabajo;
    }

    // CP01 - NORMAL: INGRESO notifica a los miembros existentes con teléfono configurado.
    // Verifica que los colaboradores distintos al afectado sí reciben el SMS.
    @Test
    @DisplayName("CP01: INGRESO notifica por SMS a los miembros con phoneNumber")
    void CP01_Ingreso_NotificaAMiembrosConPhone() {
        User lider = crearUsuario("lider", "+573001111111");
        User nuevo  = crearUsuario("nuevo", "+573002222222");

        Trabajo trabajo = crearTrabajo("Proyecto X",
                crearColaborador(lider, ColaboradorTrabajo.Rol.LIDER),
                crearColaborador(nuevo, ColaboradorTrabajo.Rol.COLABORADOR));

        when(trabajoRepository.findById(1L)).thenReturn(Optional.of(trabajo));

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                1L, "Proyecto X", TrabajoEventoDTO.TipoEvento.INGRESO,
                "nuevo", "nuevo");

        observer.actualizar(evento);

        // Solo debe notificar al líder (no al propio "nuevo")
        verify(smsService, times(1)).enviarSms(eq("+573001111111"), anyString());
        verify(smsService, never()).enviarSms(eq("+573002222222"), anyString());
    }

    // CP02 - NORMAL: SALIDA por voluntad propia -> notifica solo a los miembros restantes.
    @Test
    @DisplayName("CP02: SALIDA voluntaria notifica a miembros restantes pero no al que sale")
    void CP02_SalidaVoluntaria_NotificaARestantes() {
        User lider = crearUsuario("lider", "+573001111111");
        User saliente = crearUsuario("saliente", "+573003333333");

        Trabajo trabajo = crearTrabajo("Proyecto Y",
                crearColaborador(lider, ColaboradorTrabajo.Rol.LIDER),
                crearColaborador(saliente, ColaboradorTrabajo.Rol.COLABORADOR));

        when(trabajoRepository.findById(2L)).thenReturn(Optional.of(trabajo));

        // realizadoPor == afectado -> salida voluntaria
        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                2L, "Proyecto Y", TrabajoEventoDTO.TipoEvento.SALIDA,
                "saliente", "saliente");

        observer.actualizar(evento);

        verify(smsService, times(1)).enviarSms(eq("+573001111111"), anyString());
        verify(smsService, never()).enviarSms(eq("+573003333333"), anyString());
    }

    // CP03 - NORMAL: ELIMINADO por lider -> notifica a miembros Y al usuario removido.
    @Test
    @DisplayName("CP03: eliminacion por lider notifica a miembros y al usuario removido")
    void CP03_EliminadoPorLider_NotificaAMiembrosYAlEliminado() {
        User lider     = crearUsuario("lider",     "+573001111111");
        User eliminado = crearUsuario("eliminado", "+573004444444");
        User otro      = crearUsuario("otro",      "+573005555555");

        Trabajo trabajo = crearTrabajo("Proyecto Z",
                crearColaborador(lider,     ColaboradorTrabajo.Rol.LIDER),
                crearColaborador(eliminado, ColaboradorTrabajo.Rol.COLABORADOR),
                crearColaborador(otro,      ColaboradorTrabajo.Rol.COLABORADOR));

        when(trabajoRepository.findById(3L)).thenReturn(Optional.of(trabajo));

        // realizadoPor != afectado -> eliminacion por lider
        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                3L, "Proyecto Z", TrabajoEventoDTO.TipoEvento.SALIDA,
                "eliminado", "lider");

        observer.actualizar(evento);

        // lider y "otro" reciben el mensaje de salida del grupo
        verify(smsService, atLeastOnce()).enviarSms(eq("+573001111111"), anyString());
        verify(smsService, atLeastOnce()).enviarSms(eq("+573005555555"), anyString());
        // "eliminado" recibe mensaje personalizado de remoción
        verify(smsService, atLeastOnce()).enviarSms(eq("+573004444444"), anyString());
    }

    // CP04 - BORDE: usuario sin teléfono -> no se llama enviarSms
    @Test
    @DisplayName("CP04: usuario sin phoneNumber no recibe SMS")
    void CP04_UsuarioSinPhone_NoEnviaSms() {
        User lider = crearUsuario("lider", null); // sin teléfono
        User nuevo  = crearUsuario("nuevo", "+573002222222");

        Trabajo trabajo = crearTrabajo("Proyecto Sin Tel",
                crearColaborador(lider, ColaboradorTrabajo.Rol.LIDER),
                crearColaborador(nuevo, ColaboradorTrabajo.Rol.COLABORADOR));

        when(trabajoRepository.findById(4L)).thenReturn(Optional.of(trabajo));

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                4L, "Proyecto Sin Tel", TrabajoEventoDTO.TipoEvento.INGRESO,
                "nuevo", "nuevo");

        observer.actualizar(evento);

        verify(smsService, never()).enviarSms(anyString(), anyString());
    }

    // CP05 - BORDE: trabajo no encontrado -> no se envía ningún SMS
    @Test
    @DisplayName("CP05: trabajo no encontrado no lanza excepcion y no envia SMS")
    void CP05_TrabajoNoEncontrado_NoEnviaSms() {
        when(trabajoRepository.findById(99L)).thenReturn(Optional.empty());

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                99L, "Inexistente", TrabajoEventoDTO.TipoEvento.INGRESO,
                "alguien", "alguien");

        assertDoesNotThrow(() -> observer.actualizar(evento));
        verify(smsService, never()).enviarSms(anyString(), anyString());
    }

    // CP06 - NORMAL: el mensaje de ingreso contiene el nombre del usuario y del trabajo
    @Test
    @DisplayName("CP06: mensaje de INGRESO contiene username y nombre del trabajo")
    void CP06_MensajeIngreso_ContieneUsernameYNombreTrabajo() {
        User lider = crearUsuario("lider", "+573001111111");
        User nuevo  = crearUsuario("nuevo", "+573002222222");

        Trabajo trabajo = crearTrabajo("ProyectoAlpha",
                crearColaborador(lider, ColaboradorTrabajo.Rol.LIDER),
                crearColaborador(nuevo, ColaboradorTrabajo.Rol.COLABORADOR));

        when(trabajoRepository.findById(5L)).thenReturn(Optional.of(trabajo));

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                5L, "ProyectoAlpha", TrabajoEventoDTO.TipoEvento.INGRESO,
                "nuevo", "nuevo");

        observer.actualizar(evento);

        ArgumentCaptor<String> mensajeCaptor = ArgumentCaptor.forClass(String.class);
        verify(smsService).enviarSms(eq("+573001111111"), mensajeCaptor.capture());

        String mensajeEnviado = mensajeCaptor.getValue();
        assertTrue(mensajeEnviado.contains("nuevo"), "El mensaje debe contener el username del afectado");
        assertTrue(mensajeEnviado.contains("ProyectoAlpha"), "El mensaje debe contener el nombre del trabajo");
    }
}
