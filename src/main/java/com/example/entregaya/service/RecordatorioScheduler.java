package com.example.entregaya.service;

import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.Notificacion.TipoNotificacion;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.NotificacionRepository;
import com.example.entregaya.repository.TareaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RecordatorioScheduler {

    private static final Logger log = LoggerFactory.getLogger(RecordatorioScheduler.class);

    private final TareaRepository tareaRepository;
    private final NotificacionRepository notificacionRepository;
    private final TelegramNotificacionService telegramService;

    public RecordatorioScheduler(TareaRepository tareaRepository,
                                 NotificacionRepository notificacionRepository,
                                 TelegramNotificacionService telegramService) {
        this.tareaRepository = tareaRepository;
        this.notificacionRepository = notificacionRepository;
        this.telegramService = telegramService;
    }

    @Scheduled(cron = "0 * * * * *") // Diario a las 8:00 AM
    @Transactional
    public void enviarRecordatoriosVencimiento() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(24);

        List<Tarea> tareas = tareaRepository.findTareasProximasAVencer(ahora, limite);

        for (Tarea tarea : tareas) {
            for (User responsable : tarea.getResponsables()) {
                // Notificación interna
                String mensaje = "La tarea '" + tarea.getNombre() + "' vence mañana.";
                Notificacion n = new Notificacion(responsable, mensaje, TipoNotificacion.RECORDATORIO_VENCIMIENTO);
                notificacionRepository.save(n);

                // Notificación Telegram
                //La hu 2 integra telegramId en user
                if (responsable.getTelegramChatId() != null && !responsable.getTelegramChatId().isBlank()) {
                    telegramService.enviarMensaje(responsable.getTelegramChatId(), mensaje);
                    log.info("[Scheduler] Telegram enviado a usuario={}", responsable.getUsername());
                }
            }
            tarea.setRecordatorioEnviado(true);
            tareaRepository.save(tarea);
        }
    }
}