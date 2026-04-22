package com.example.entregaya.facade;

import com.example.entregaya.dto.DashboardDTO;
import com.example.entregaya.model.Invitacion;
import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.NotificacionRepository;
import com.example.entregaya.repository.UserRepository;
import com.example.entregaya.service.CustomInvitacionDetailsService;
import com.example.entregaya.service.CustomTareaDetailsService;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facade que centraliza los cálculos del dashboard.
 * Incluye el conteo de notificaciones no leídas (DoD D6).
 */
@Component
public class DashboardFacade {

    private final CustomTrabajoDetailsService customTrabajoDetailsService;
    private final CustomTareaDetailsService   customTareaDetailsService;
    private final CustomInvitacionDetailsService customInvitacionDetailsService;
    private final NotificacionRepository      notificacionRepository;
    private final UserRepository              userRepository;

    public DashboardFacade(CustomTrabajoDetailsService customTrabajoDetailsService,
                           CustomTareaDetailsService customTareaDetailsService,
                           CustomInvitacionDetailsService customInvitacionDetailsService,
                           NotificacionRepository notificacionRepository,
                           UserRepository userRepository) {
        this.customTrabajoDetailsService     = customTrabajoDetailsService;
        this.customTareaDetailsService       = customTareaDetailsService;
        this.customInvitacionDetailsService  = customInvitacionDetailsService;
        this.notificacionRepository          = notificacionRepository;
        this.userRepository                  = userRepository;
    }

    public DashboardDTO getDashboardData(String username) {

        // 1. Todos los trabajos del usuario
        List<Trabajo> todos = customTrabajoDetailsService.listarPorUsuario(username);

        // 2. Mapa de progresos (trabajoId → 0-100)
        Map<Long, Integer> progresos = new HashMap<>();
        for (Trabajo trabajo : todos) {
            progresos.put(trabajo.getId(),
                    customTareaDetailsService.calcularProgreso(trabajo.getId()));
        }

        // 3. Trabajos activos (progreso < 100%)
        List<Trabajo> activos = todos.stream()
                .filter(t -> progresos.get(t.getId()) < 100)
                .toList();

        // 4. Próximos a vencer (≤ 7 días)
        List<Trabajo> proximosVencer = activos.stream()
                .filter(t -> t.getFechaEntrega() != null)
                .filter(t -> {
                    LocalDate fechaEntrega = t.getFechaEntrega().toLocalDate();
                    LocalDate ahora  = LocalDate.now();
                    LocalDate limite = ahora.plusDays(7);
                    return !fechaEntrega.isBefore(ahora) && !fechaEntrega.isAfter(limite);
                })
                .toList();

        // 5. Tareas vencidas (HU-11)
        LocalDateTime ahora = LocalDateTime.now();
        long tareasVencidasCount = todos.stream()
                .flatMap(t -> customTareaDetailsService.tareas(t.getId()).stream())
                .filter(tarea -> !tarea.getIsCompletada()
                        && tarea.getFechaFinal() != null
                        && tarea.getFechaFinal().isBefore(ahora))
                .count();

        // 6. Invitaciones pendientes
        List<Invitacion> invitaciones =
                customInvitacionDetailsService.pendientesParaUsuario(username);

        // 7. Notificaciones no leídas (DoD D6)
        long notificacionesNoLeidas = userRepository.findByUsername(username)
                .map(user -> notificacionRepository
                        .findByDestinatarioAndLeidaFalseOrderByFechaCreacionDesc(user)
                        .size())
                .orElse(0);

        return new DashboardDTO(
                activos,
                progresos,
                todos.size(),
                todos.size() - activos.size(),
                proximosVencer,
                invitaciones,
                tareasVencidasCount,
                notificacionesNoLeidas
        );
    }

    /**
     * HU-37: Estadísticas personales del usuario autenticado.
     * Calcula total de tareas, completadas, vencidas y tasa de completitud.
     */
    public Map<String, Object> getEstadisticasPersonales(String username) {

        List<Trabajo> trabajos = customTrabajoDetailsService.listarPorUsuario(username);

        List<com.example.entregaya.model.Tarea> todasLasTareas = trabajos.stream()
                .flatMap(t -> customTareaDetailsService.tareas(t.getId()).stream())
                .toList();

        long total       = todasLasTareas.size();
        long completadas = todasLasTareas.stream()
                .filter(com.example.entregaya.model.Tarea::getIsCompletada)
                .count();
        long vencidas    = todasLasTareas.stream()
                .filter(t -> !t.getIsCompletada()
                        && t.getFechaFinal() != null
                        && t.getFechaFinal().isBefore(LocalDateTime.now()))
                .count();
        double tasa = total == 0 ? 0.0
                : Math.round((completadas * 100.0 / total) * 10.0) / 10.0;

        return Map.of(
                "totalTareas",       total,
                "tareasCompletadas", completadas,
                "tareasVencidas",    vencidas,
                "tasaCompletitud",   tasa
        );
    }
}