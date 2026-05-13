package com.example.entregaya.observer;

import com.example.entregaya.dto.TrabajoEventoDTO;
import com.example.entregaya.model.ColaboradorTrabajo;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.TrabajoRepository;
import com.example.entregaya.service.TelegramNotificacionService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TelegramMiembroObserver - Tests unitarios")
class TelegramMiembroObserverTest {

    @Mock
    private TelegramNotificacionService telegramService;

    @Mock
    private TrabajoRepository trabajoRepository;

    @InjectMocks
    private TelegramMiembroObserver observer;

    // ── Helpers ────────────────────────────────────────────────────────────

    private User crearUsuario(String username, String chatId) {
        User user = new User();
        user.setUsername(username);
        user.setTelegramChatId(chatId);
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

    // ── CP01 ───────────────────────────────────────────────────────────────
    // NORMAL: INGRESO notifica a los miembros existentes con chatId configurado.
    // Verifica que los colaboradores distintos al afectado sí reciben el mensaje.
    // Entrada: trabajo con 2 miembros, evento INGRESO del segundo.
    // Esperado: enviarMensaje llamado 1 vez (solo al primer miembro).
    @Test
    @DisplayName("CP01: INGRESO notifica a los miembros existentes con chatId")
    void CP01_Ingreso_NotificaAMiembrosConChatId() {
        User lider = crearUsuario("lider", "chat-lider");
        User nuevo  = crearUsuario("nuevo", "chat-nuevo");

        Trabajo trabajo = crearTrabajo("Proyecto X",
                crearColaborador(lider, ColaboradorTrabajo.Rol.LIDER),
                crearColaborador(nuevo, ColaboradorTrabajo.Rol.COLABORADOR));

        when(trabajoRepository.findById(1L)).thenReturn(Optional.of(trabajo));

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                1L, "Proyecto X", TrabajoEventoDTO.TipoEvento.INGRESO,
                "nuevo", "nuevo", LocalDateTime.now());

        observer.actualizar(evento);

        verify(telegramService, times(1)).enviarMensaje(eq("chat-lider"), anyString());
        verify(telegramService, never()).enviarMensaje(eq("chat-nuevo"), anyString());
    }

    // ── CP02 ───────────────────────────────────────────────────────────────
    // NORMAL: El mensaje de INGRESO contiene "se unió al trabajo".
    // Verifica que el texto comunicado es correcto según el tipo de evento.
    // Entrada: evento INGRESO con nombreTrabajo = "Proyecto Alpha".
    // Esperado: mensaje contiene "se unió al trabajo" y el nombre del trabajo.
    @Test
    @DisplayName("CP02: INGRESO envía mensaje con 'se unió al trabajo'")
    void CP02_Ingreso_MensajeContieneSeUnio() {
        User lider = crearUsuario("lider", "chat-lider");
        User nuevo  = crearUsuario("nuevo", "chat-nuevo");

        Trabajo trabajo = crearTrabajo("Proyecto Alpha",
                crearColaborador(lider, ColaboradorTrabajo.Rol.LIDER),
                crearColaborador(nuevo, ColaboradorTrabajo.Rol.COLABORADOR));

        when(trabajoRepository.findById(1L)).thenReturn(Optional.of(trabajo));

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                1L, "Proyecto Alpha", TrabajoEventoDTO.TipoEvento.INGRESO,
                "nuevo", "nuevo", LocalDateTime.now());

        ArgumentCaptor<String> mensajeCaptor = ArgumentCaptor.forClass(String.class);
        observer.actualizar(evento);

        verify(telegramService).enviarMensaje(anyString(), mensajeCaptor.capture());
        String mensaje = mensajeCaptor.getValue();
        assertTrue(mensaje.contains("se unió al trabajo"));
        assertTrue(mensaje.contains("Proyecto Alpha"));
    }

    // ── CP03 ───────────────────────────────────────────────────────────────
    // NORMAL: SALIDA voluntaria notifica a los miembros restantes.
    // Verifica que cuando el afectado == realizadoPor no se envía mensaje extra al removido.
    // Entrada: evento SALIDA donde afectadoUsername == realizadoPorUsername.
    // Esperado: enviarMensaje solo al miembro restante, no al que salió.
    @Test
    @DisplayName("CP03: SALIDA voluntaria notifica a miembros restantes sin mensaje extra al afectado")
    void CP03_SalidaVoluntaria_NotificaSoloARestantes() {
        User lider    = crearUsuario("lider", "chat-lider");
        User saliente = crearUsuario("saliente", "chat-saliente");

        Trabajo trabajo = crearTrabajo("Proyecto Beta",
                crearColaborador(lider, ColaboradorTrabajo.Rol.LIDER),
                crearColaborador(saliente, ColaboradorTrabajo.Rol.COLABORADOR));

        when(trabajoRepository.findById(2L)).thenReturn(Optional.of(trabajo));

        // afectado == realizadoPor → salida voluntaria
        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                2L, "Proyecto Beta", TrabajoEventoDTO.TipoEvento.SALIDA,
                "saliente", "saliente", LocalDateTime.now());

        observer.actualizar(evento);

        // Solo 1 mensaje: al lider
        verify(telegramService, times(1)).enviarMensaje(anyString(), anyString());
        verify(telegramService).enviarMensaje(eq("chat-lider"), anyString());
    }

    // ── CP04 ───────────────────────────────────────────────────────────────
    // NORMAL: El mensaje de SALIDA contiene "abandonó el trabajo".
    // Verifica que el texto es el correcto para eventos de salida.
    // Entrada: evento SALIDA con nombreTrabajo = "Proyecto Gamma".
    // Esperado: mensaje contiene "abandonó el trabajo" y el nombre del trabajo.
    @Test
    @DisplayName("CP04: SALIDA envía mensaje con 'abandonó el trabajo'")
    void CP04_Salida_MensajeContieneAbandono() {
        User lider    = crearUsuario("lider", "chat-lider");
        User saliente = crearUsuario("saliente", "chat-saliente");

        Trabajo trabajo = crearTrabajo("Proyecto Gamma",
                crearColaborador(lider, ColaboradorTrabajo.Rol.LIDER),
                crearColaborador(saliente, ColaboradorTrabajo.Rol.COLABORADOR));

        when(trabajoRepository.findById(3L)).thenReturn(Optional.of(trabajo));

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                3L, "Proyecto Gamma", TrabajoEventoDTO.TipoEvento.SALIDA,
                "saliente", "saliente", LocalDateTime.now());

        ArgumentCaptor<String> mensajeCaptor = ArgumentCaptor.forClass(String.class);
        observer.actualizar(evento);

        verify(telegramService).enviarMensaje(eq("chat-lider"), mensajeCaptor.capture());
        assertTrue(mensajeCaptor.getValue().contains("abandonó el trabajo"));
        assertTrue(mensajeCaptor.getValue().contains("Proyecto Gamma"));
    }

    // ── CP05 ───────────────────────────────────────────────────────────────
    // NORMAL: Eliminación por líder notifica al usuario removido con mensaje personalizado.
    // Verifica el caso en que afectado != realizadoPor (eliminación forzosa).
    // Entrada: evento SALIDA donde realizadoPorUsername = "lider" y afectadoUsername = "removido".
    // Esperado: enviarMensaje al removido con mensaje que incluye "Fuiste eliminado" y el nombre del líder.
    @Test
    @DisplayName("CP05: Eliminación por líder notifica al usuario removido con quién lo eliminó")
    void CP05_EliminacionPorLider_NotificaAlRemovido() {
        User lider   = crearUsuario("lider", "chat-lider");
        User removido = crearUsuario("removido", "chat-removido");

        Trabajo trabajo = crearTrabajo("Proyecto Delta",
                crearColaborador(lider, ColaboradorTrabajo.Rol.LIDER),
                crearColaborador(removido, ColaboradorTrabajo.Rol.COLABORADOR));

        when(trabajoRepository.findById(4L)).thenReturn(Optional.of(trabajo));

        // afectado != realizadoPor → eliminación por líder
        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                4L, "Proyecto Delta", TrabajoEventoDTO.TipoEvento.SALIDA,
                "removido", "lider", LocalDateTime.now());

        observer.actualizar(evento);

        ArgumentCaptor<String> mensajeCaptor = ArgumentCaptor.forClass(String.class);
        verify(telegramService).enviarMensaje(eq("chat-removido"), mensajeCaptor.capture());
        assertTrue(mensajeCaptor.getValue().contains("Fuiste eliminado"));
        assertTrue(mensajeCaptor.getValue().contains("lider"));
    }

    // ── CP06 ───────────────────────────────────────────────────────────────
    // NORMAL: Eliminación por líder envía en total 2 mensajes (miembros + removido).
    // Verifica que la notificación al removido es adicional a la de los demás miembros.
    // Entrada: trabajo con 3 miembros, evento SALIDA forzosa del tercero.
    // Esperado: enviarMensaje llamado 3 veces (2 miembros restantes + 1 removido).
    @Test
    @DisplayName("CP06: Eliminación por líder envía mensaje a miembros restantes y al removido")
    void CP06_EliminacionPorLider_EnviaMensajeARestantesYAlRemovido() {
        User lider    = crearUsuario("lider", "chat-lider");
        User editor   = crearUsuario("editor", "chat-editor");
        User removido = crearUsuario("removido", "chat-removido");

        Trabajo trabajo = crearTrabajo("Proyecto Épsilon",
                crearColaborador(lider, ColaboradorTrabajo.Rol.LIDER),
                crearColaborador(editor, ColaboradorTrabajo.Rol.EDITOR),
                crearColaborador(removido, ColaboradorTrabajo.Rol.COLABORADOR));

        when(trabajoRepository.findById(5L)).thenReturn(Optional.of(trabajo));

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                5L, "Proyecto Épsilon", TrabajoEventoDTO.TipoEvento.SALIDA,
                "removido", "lider", LocalDateTime.now());

        observer.actualizar(evento);

        // lider + editor (notif de salida) + removido (notif de remoción) = 3
        verify(telegramService, times(3)).enviarMensaje(anyString(), anyString());
    }

    // ── CP07 ───────────────────────────────────────────────────────────────
    // BORDE: Miembro sin chatId configurado no recibe mensaje.
    // Verifica que el observer omite silenciosamente usuarios sin Telegram vinculado.
    // Entrada: miembro con telegramChatId = null.
    // Esperado: enviarMensaje no se llama para ese usuario.
    @Test
    @DisplayName("CP07: Miembro con chatId null no recibe notificación")
    void CP07_MiembroSinChatId_NuloNoEnviaMensaje() {
        User lider = crearUsuario("lider", null);   // sin chatId
        User nuevo = crearUsuario("nuevo", "chat-nuevo");

        Trabajo trabajo = crearTrabajo("Proyecto Zeta",
                crearColaborador(lider, ColaboradorTrabajo.Rol.LIDER),
                crearColaborador(nuevo, ColaboradorTrabajo.Rol.COLABORADOR));

        when(trabajoRepository.findById(6L)).thenReturn(Optional.of(trabajo));

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                6L, "Proyecto Zeta", TrabajoEventoDTO.TipoEvento.INGRESO,
                "nuevo", "nuevo", LocalDateTime.now());

        observer.actualizar(evento);

        verify(telegramService, never()).enviarMensaje(anyString(), anyString());
    }

    // ── CP08 ───────────────────────────────────────────────────────────────
    // BORDE: Miembro con chatId en blanco no recibe mensaje.
    // Verifica que un chatId vacío ("" o "   ") se trata igual que null.
    // Entrada: miembro con telegramChatId = "   ".
    // Esperado: enviarMensaje no se llama.
    @Test
    @DisplayName("CP08: Miembro con chatId en blanco no recibe notificación")
    void CP08_MiembroConChatIdEnBlanco_NoEnviaMensaje() {
        User lider = crearUsuario("lider", "   ");  // en blanco
        User nuevo = crearUsuario("nuevo", "chat-nuevo");

        Trabajo trabajo = crearTrabajo("Proyecto Eta",
                crearColaborador(lider, ColaboradorTrabajo.Rol.LIDER),
                crearColaborador(nuevo, ColaboradorTrabajo.Rol.COLABORADOR));

        when(trabajoRepository.findById(7L)).thenReturn(Optional.of(trabajo));

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                7L, "Proyecto Eta", TrabajoEventoDTO.TipoEvento.INGRESO,
                "nuevo", "nuevo", LocalDateTime.now());

        observer.actualizar(evento);

        verify(telegramService, never()).enviarMensaje(anyString(), anyString());
    }

    // ── CP09 ───────────────────────────────────────────────────────────────
    // NEGATIVA: trabajoId inexistente no produce ninguna notificación.
    // Verifica la resiliencia del observer ante IDs inválidos o trabajos eliminados.
    // Entrada: findById retorna Optional.empty().
    // Esperado: enviarMensaje nunca se llama.
    @Test
    @DisplayName("CP09: trabajoId inexistente no envía ningún mensaje")
    void CP09_TrabajoInexistente_NoEnviaMensaje() {
        when(trabajoRepository.findById(999L)).thenReturn(Optional.empty());

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                999L, "Fantasma", TrabajoEventoDTO.TipoEvento.INGRESO,
                "usuario", "usuario", LocalDateTime.now());

        observer.actualizar(evento);

        verify(telegramService, never()).enviarMensaje(anyString(), anyString());
    }

    // ── CP10 ───────────────────────────────────────────────────────────────
    // BORDE: Eliminación por líder donde el removido ya no está en la lista de colaboradores.
    // Verifica que ifPresent no falla cuando el afectado no aparece en getColaboradores().
    // Entrada: evento SALIDA forzosa de un usuario que no está en la lista de colaboradores.
    // Esperado: solo se notifica al miembro restante, sin excepción ni mensaje extra.
    @Test
    @DisplayName("CP10: Eliminación de usuario no presente en colaboradores no lanza excepción")
    void CP10_EliminacionDeUsuarioAusenteEnColaboradores_NoFalla() {
        User lider = crearUsuario("lider", "chat-lider");

        // "removido" no está en la lista de colaboradores del trabajo
        Trabajo trabajo = crearTrabajo("Proyecto Theta",
                crearColaborador(lider, ColaboradorTrabajo.Rol.LIDER));

        when(trabajoRepository.findById(8L)).thenReturn(Optional.of(trabajo));

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                8L, "Proyecto Theta", TrabajoEventoDTO.TipoEvento.SALIDA,
                "removido", "lider", LocalDateTime.now());

        assertDoesNotThrow(() -> observer.actualizar(evento));

        // Solo el lider recibe la notificación de salida
        verify(telegramService, times(1)).enviarMensaje(eq("chat-lider"), anyString());
    }

    // ── CP11 ───────────────────────────────────────────────────────────────
    // BORDE: Eliminación por líder donde el removido tiene chatId null.
    // Verifica que no se intenta enviar mensaje al removido si no tiene Telegram vinculado.
    // Entrada: removido con telegramChatId = null, evento SALIDA forzosa.
    // Esperado: solo se notifica a los miembros restantes con chatId.
    @Test
    @DisplayName("CP11: Eliminación por líder donde el removido no tiene chatId no envía mensaje extra")
    void CP11_EliminacionPorLider_RemovidoSinChatId_NoEnviaMensajeExtra() {
        User lider   = crearUsuario("lider", "chat-lider");
        User removido = crearUsuario("removido", null);  // sin chatId

        Trabajo trabajo = crearTrabajo("Proyecto Iota",
                crearColaborador(lider, ColaboradorTrabajo.Rol.LIDER),
                crearColaborador(removido, ColaboradorTrabajo.Rol.COLABORADOR));

        when(trabajoRepository.findById(9L)).thenReturn(Optional.of(trabajo));

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                9L, "Proyecto Iota", TrabajoEventoDTO.TipoEvento.SALIDA,
                "removido", "lider", LocalDateTime.now());

        observer.actualizar(evento);

        // Solo el lider recibe mensaje (el removido no tiene chatId)
        verify(telegramService, times(1)).enviarMensaje(eq("chat-lider"), anyString());
    }

    // ── CP12 ───────────────────────────────────────────────────────────────
    // NORMAL: Trabajo sin miembros distintos al afectado no envía ningún mensaje.
    // Verifica el caso extremo de un trabajo donde solo está el propio afectado.
    // Entrada: trabajo con 1 solo colaborador que es el afectado, evento INGRESO.
    // Esperado: enviarMensaje nunca se llama.
    @Test
    @DisplayName("CP12: Trabajo con un solo miembro (el afectado) no envía mensajes")
    void CP12_TrabajoConUnSoloMiembro_NoEnviaMensajes() {
        User unico = crearUsuario("unico", "chat-unico");

        Trabajo trabajo = crearTrabajo("Proyecto Kappa",
                crearColaborador(unico, ColaboradorTrabajo.Rol.LIDER));

        when(trabajoRepository.findById(10L)).thenReturn(Optional.of(trabajo));

        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                10L, "Proyecto Kappa", TrabajoEventoDTO.TipoEvento.INGRESO,
                "unico", "unico", LocalDateTime.now());

        observer.actualizar(evento);

        verify(telegramService, never()).enviarMensaje(anyString(), anyString());
    }
}
