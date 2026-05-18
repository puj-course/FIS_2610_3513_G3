package com.example.entregaya.observer;

import com.example.entregaya.dto.TrabajoEventoDTO;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.repository.TrabajoRepository;
import com.example.entregaya.service.TwilioSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Observer que envía notificaciones por SMS (Twilio) cuando un miembro
 * se une o es eliminado de un trabajo.
 *
 * Reemplaza TelegramMiembroObserver. Usa el campo phoneNumber del User
 * en lugar del telegramChatId.
 *
 * Notifica a:
 * - Todos los miembros del trabajo (excepto el afectado) cuando alguien ingresa o sale.
 * - Al usuario eliminado cuando es removido por un líder.
 */
@Component
public class SmsMiembroObserver implements TrabajoObserver {

    private static final Logger log = LoggerFactory.getLogger(SmsMiembroObserver.class);

    private final TwilioSmsService smsService;
    private final TrabajoRepository trabajoRepository;

    public SmsMiembroObserver(TwilioSmsService smsService,
                               TrabajoRepository trabajoRepository) {
        this.smsService = smsService;
        this.trabajoRepository = trabajoRepository;
    }

    @Override
    public void actualizar(TrabajoEventoDTO evento) {
        Trabajo trabajo = trabajoRepository.findById(evento.trabajoId()).orElse(null);
        if (trabajo == null) {
            log.warn("[SmsMiembro] Trabajo con id={} no encontrado. No se envían SMS.", evento.trabajoId());
            return;
        }

        boolean esIngreso = evento.tipoEvento() == TrabajoEventoDTO.TipoEvento.INGRESO;

        String mensajeParaMiembros = esIngreso
                ? String.format("[EntregaYa] %s se unió al trabajo \"%s\".",
                        evento.afectadoUsername(), evento.nombreTrabajo())
                : String.format("[EntregaYa] %s abandonó el trabajo \"%s\".",
                        evento.afectadoUsername(), evento.nombreTrabajo());

        // Notificar por SMS a todos los miembros actuales excepto al afectado
        trabajo.getColaboradores().stream()
                .filter(c -> !c.getUser().getUsername().equals(evento.afectadoUsername()))
                .forEach(colaborador -> {
                    String phone = colaborador.getUser().getPhoneNumber();
                    if (phone != null && !phone.isBlank()) {
                        smsService.enviarSms(phone, mensajeParaMiembros);
                        log.info("[SmsMiembro] SMS enviado a usuario={}", colaborador.getUser().getUsername());
                    }
                });

        // Si fue eliminado por un líder (realizadoPor != afectado), notificar al usuario removido
        boolean fueEliminadoPorOtro = !evento.afectadoUsername().equals(evento.realizadoPorUsername());
        if (!esIngreso && fueEliminadoPorOtro) {
            trabajo.getColaboradores().stream()
                    .filter(c -> c.getUser().getUsername().equals(evento.afectadoUsername()))
                    .findFirst()
                    .ifPresent(c -> {
                        String phone = c.getUser().getPhoneNumber();
                        if (phone != null && !phone.isBlank()) {
                            String mensajeAlEliminado = String.format(
                                    "[EntregaYa] Fuiste eliminado del trabajo \"%s\" por %s.",
                                    evento.nombreTrabajo(),
                                    evento.realizadoPorUsername()
                            );
                            smsService.enviarSms(phone, mensajeAlEliminado);
                            log.info("[SmsMiembro] SMS de remoción enviado a usuario={}", evento.afectadoUsername());
                        }
                    });
        }
    }
}
