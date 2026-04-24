package com.example.entregaya.service;

import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.Notificacion.TipoNotificacion;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.NotificacionRepository;
import com.example.entregaya.repository.TareaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RecordatorioScheduler {

    private final TareaRepository tareaRepository;
    private final NotificacionRepository notificacionRepository;

    public RecordatorioScheduler(TareaRepository tareaRepository,
                                 NotificacionRepository notificacionRepository) {
        this.tareaRepository = tareaRepository;
        this.notificacionRepository = notificacionRepository;
    }

    @Scheduled(cron = "0 0 8 * * *") // Diario a las 8:00 AM
    public void enviarRecordatoriosVencimiento() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(24);

        List<Tarea> tareas = tareaRepository.findTareasProximasAVencer(ahora, limite);

        for (Tarea tarea : tareas) {
            for (User responsable : tarea.getResponsables()) {
                String mensaje = "La tarea '" + tarea.getNombre() + "' vence mañana.";
                Notificacion n = new Notificacion(responsable, mensaje, TipoNotificacion.RECORDATORIO_VENCIMIENTO);
                notificacionRepository.save(n);
            }
            tarea.setRecordatorioEnviado(true);
            tareaRepository.save(tarea);
        }
    }
}