package com.example.entregaya.facade;

import com.example.entregaya.dto.DashboardDTO;
import com.example.entregaya.model.Invitacion;
import com.example.entregaya.model.Trabajo;
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
 *
 * Encapsula la lógica que antes vivía directamente en AuthController#dashboard(),
 * y la expone a través del método {@link #getDashboardData(String)}.
 * El controlador queda reducido a delegar en este componente y poblar el modelo.
 */
@Component
public class DashboardFacade {

    private final CustomTrabajoDetailsService customTrabajoDetailsService;
    private final CustomTareaDetailsService customTareaDetailsService;
    private final CustomInvitacionDetailsService customInvitacionDetailsService;

    public DashboardFacade(CustomTrabajoDetailsService customTrabajoDetailsService,
                           CustomTareaDetailsService customTareaDetailsService,
                           CustomInvitacionDetailsService customInvitacionDetailsService) {
        this.customTrabajoDetailsService = customTrabajoDetailsService;
        this.customTareaDetailsService = customTareaDetailsService;
        this.customInvitacionDetailsService = customInvitacionDetailsService;
    }

    /**
     * Calcula y devuelve todos los datos necesarios para el dashboard del usuario indicado.
     *
     * Los valores retornados son idénticos a los que el controlador pasaba
     * al modelo de Thymeleaf antes de esta refactorización:
     * <ul>
     *   <li>{@code trabajos}       – trabajos activos (progreso &lt; 100 %)</li>
     *   <li>{@code progresos}      – mapa trabajoId → porcentaje de progreso</li>
     *   <li>{@code totalTrabajos}  – cantidad total de trabajos del usuario</li>
     *   <li>{@code completados}    – trabajos con progreso == 100 %</li>
     *   <li>{@code proximosVencer} – activos cuya entrega es en ≤ 7 días</li>
     *   <li>{@code invitaciones}   – invitaciones pendientes para el usuario</li>
     *   <li>{@code tareasVencidas} – tareas no completadas con fechaFinal pasada</li>
     * </ul>
     *
     * @param username nombre de usuario autenticado
     * @return {@link DashboardDTO} con los 7 campos del dashboard
     */
    public DashboardDTO getDashboardData(String username) {

        // 1. Todos los trabajos del usuario
        List<Trabajo> todos = customTrabajoDetailsService.listarPorUsuario(username);

        // 2. Mapa de progresos (trabajoId → 0-100)
        Map<Long, Integer> progresos = new HashMap<>();
        for (Trabajo trabajo : todos) {
            progresos.put(trabajo.getId(), customTareaDetailsService.calcularProgreso(trabajo.getId()));
        }

        // 3. Trabajos activos (progreso < 100%)
        List<Trabajo> activos = todos.stream()
                .filter(trabajo -> progresos.get(trabajo.getId()) < 100)
                .toList();

        // 4. Trabajos próximos a vencer (en los próximos 7 días)
        List<Trabajo> proximosVencer = activos.stream()
                .filter(trabajo -> trabajo.getFechaEntrega() != null)
                .filter(trabajo -> {
                    LocalDate fechaEntrega = trabajo.getFechaEntrega().toLocalDate();
                    LocalDate ahora = LocalDate.now();
                    LocalDate limite = ahora.plusDays(7);
                    return !fechaEntrega.isBefore(ahora) && !fechaEntrega.isAfter(limite);
                })
                .toList();

        // 5. HU-11: Tareas vencidas del usuario (no completadas con fechaFinal pasada)
        LocalDateTime ahora = LocalDateTime.now();
        long tareasVencidasCount = todos.stream()
                .flatMap(trabajo -> customTareaDetailsService.tareas(trabajo.getId()).stream())
                .filter(tarea -> !tarea.getIsCompletada()
                        && tarea.getFechaFinal() != null
                        && tarea.getFechaFinal().isBefore(ahora))
                .count();

        // 6. Invitaciones pendientes
        List<Invitacion> invitaciones = customInvitacionDetailsService.pendientesParaUsuario(username);

        return new DashboardDTO(
                activos,
                progresos,
                todos.size(),
                todos.size() - activos.size(),
                proximosVencer,
                invitaciones,
                tareasVencidasCount
        );
    }
}
