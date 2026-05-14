package com.example.entregaya.observer;

import com.example.entregaya.dto.TrabajoEventoDTO;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.repository.TrabajoRepository;
import com.example.entregaya.service.TelegramNotificacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Observer que envía notificaciones por Telegram cuando un miembro
 * se une o es eliminado de un trabajo.
 *
 * Notifica a:
 * - Todos los miembros del trabajo (excepto el afectado) cuando alguien ingresa o sale.
 * - Al usuario eliminado cuando es removido por un líder.
 */
@Component
public class TelegramMiembroObserver implements TrabajoObserver {

    private static final Logger log = LoggerFactory.getLogger(TelegramMiembroObserver.class);

    private final TelegramNotificacionService telegramService;
    private final TrabajoRepository trabajoRepository;

    public TelegramMiembroObserver(TelegramNotificacionService telegramService,
                                   TrabajoRepository trabajoRepository) {
        this.telegramService = telegramService;
        this.trabajoRepository = trabajoRepository;
    }

    @Override
    public void actualizar(TrabajoEventoDTO evento) {
        Trabajo trabajo = trabajoRepository.findById(evento.trabajoId()).orElse(null);
        if (trabajo == null) {
            return;
        }

        boolean esIngreso = evento.tipoEvento() == TrabajoEventoDTO.TipoEvento.INGRESO;

        String mensajeParaMiembros = esIngreso
                ? String.format("%s se unió al trabajo \"%s\".", evento.afectadoUsername(), evento.nombreTrabajo())
                : String.format("%s abandonó el trabajo \"%s\".", evento.afectadoUsername(), evento.nombreTrabajo());

        // Notificar a todos los miembros actuales excepto al afectado
        trabajo.getColaboradores().stream()
                .filter(c -> !c.getUser().getUsername().equals(evento.afectadoUsername()))
                .forEach(colaborador -> {
                    String chatId = colaborador.getUser().getTelegramChatId();
                    if (chatId != null && !chatId.isBlank()) {
                        telegramService.enviarMensaje(chatId, mensajeParaMiembros);
                        log.info("[TelegramMiembro] Notificación enviada a usuario={}", colaborador.getUser().getUsername());
                    }
                });

        // Si fue eliminado por un líder (realizadoPor != afectado), notificar al usuario removido
        boolean fueEliminadoPorOtro = !evento.afectadoUsername().equals(evento.realizadoPorUsername());
        if (!esIngreso && fueEliminadoPorOtro) {
            trabajo.getColaboradores().stream()
                    .filter(c -> c.getUser().getUsername().equals(evento.afectadoUsername()))
                    .findFirst()
                    .ifPresent(c -> {
                        String chatId = c.getUser().getTelegramChatId();
                        if (chatId != null && !chatId.isBlank()) {
                            String mensajeAlEliminado = String.format(
                                    "Fuiste eliminado del trabajo \"%s\" por %s.",
                                    evento.nombreTrabajo(),
                                    evento.realizadoPorUsername()
                            );
                            telegramService.enviarMensaje(chatId, mensajeAlEliminado);
                            log.info("[TelegramMiembro] Notificación de remoción enviada a usuario={}", evento.afectadoUsername());
                        }
                    });
        }
    }
}
