package com.example.entregaya.controller;


import com.example.entregaya.dto.MiembroRolDTO;
import com.example.entregaya.model.ColaboradorTrabajo;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.service.CustomInvitacionDetailsService;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import com.example.entregaya.service.CustomTareaDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/trabajos")
public class TrabajoController {

    private final CustomTrabajoDetailsService customTrabajoDetailsService;
    private final CustomTareaDetailsService customTareaDetailsService;
    private final CustomInvitacionDetailsService customInvitacionDetailsService;

    public TrabajoController(CustomTrabajoDetailsService customTrabajoDetailsService,
                             CustomTareaDetailsService customTareaDetailsService, CustomInvitacionDetailsService customInvitacionDetailsService) {
        this.customTrabajoDetailsService = customTrabajoDetailsService;
        this.customTareaDetailsService = customTareaDetailsService;
        this.customInvitacionDetailsService = customInvitacionDetailsService;
    }

    @GetMapping
    public String trabajo(Model model, @AuthenticationPrincipal UserDetails user) {
        List<Trabajo>trabajos= customTrabajoDetailsService.listarPorUsuario(user.getUsername());
        Map<Long,Integer> progresos = new HashMap<>();
        for(Trabajo t : trabajos){
            progresos.put(t.getId(), customTareaDetailsService.calcularProgreso(t.getId()));
        }

        model.addAttribute("trabajos", trabajos);
        model.addAttribute("progresos", progresos);
        return "trabajos/lista";
    }

    @GetMapping("/nuevo")
    public String formulario(Model model) {
        model.addAttribute("trabajo", new Trabajo());
        return "trabajos/formulario";
    }
    @PostMapping("/nuevo")
    public String guardar(@ModelAttribute Trabajo trabajo, @AuthenticationPrincipal UserDetails user) {
        customTrabajoDetailsService.crearTrabajo(trabajo,user.getUsername());
        return "redirect:/trabajos";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable long id, Model model) {
        Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(id);
        List<Tarea> tareas = customTareaDetailsService.tareas(id);

        trabajo.getColaboradores().size();
        tareas.forEach(t -> t.getResponsables().size());

        // Calcular estadísticas
        long completadas = tareas.stream().filter(Tarea::getIsCompletada).count();
        long pendientes = tareas.stream()
                .filter(t -> !t.getIsCompletada() &&
                        (t.getFechaInicio() == null || t.getFechaInicio().isAfter(java.time.LocalDateTime.now())))
                .count();
        long enProgreso = tareas.stream()
                .filter(t -> !t.getIsCompletada() &&
                        t.getFechaInicio() != null &&
                        !t.getFechaInicio().isAfter(java.time.LocalDateTime.now()))
                .count();

        // Obtener próximas entregas (tareas no completadas ordenadas por fecha)
        List<Tarea> proximasEntregas = tareas.stream()
                .filter(t -> !t.getIsCompletada() && t.getFechaFinal() != null)
                .sorted((t1, t2) -> t1.getFechaFinal().compareTo(t2.getFechaFinal()))
                .limit(5)
                .toList();

        // Miembros con roles para mostrar en la vista de detalle
        List<MiembroRolDTO> miembros = customTrabajoDetailsService.consultarMiembros(id);

        model.addAttribute("trabajo", trabajo);
        model.addAttribute("tareas", tareas);
        model.addAttribute("progreso", customTareaDetailsService.calcularProgreso(id));
        model.addAttribute("completadas", completadas);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("enProgreso", enProgreso);
        model.addAttribute("proximasEntregas", proximasEntregas);
        model.addAttribute("invitaciones", customInvitacionDetailsService.porTrabajo(id));
        model.addAttribute("miembros", miembros);
        return "trabajos/detalle";
    }

    @GetMapping("/{id}/miembros")
    @ResponseBody
    public ResponseEntity<List<MiembroRolDTO>> miembros(@PathVariable long id) {
        List<MiembroRolDTO> miembros = customTrabajoDetailsService.consultarMiembros(id);
        return ResponseEntity.ok(miembros);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar (@PathVariable long id) {
        customTrabajoDetailsService.eliminar(id);
        return "redirect:/trabajos";
    }

    @GetMapping("/CrearTarea")
    public String CrearTarea(Model model) {
        model.addAttribute("tarea", new Tarea());
        return "trabajos/CrearTarea";
    }

    @GetMapping("/trabajos-especificos")
    public String TrabajosEspecificos(Model model) {
        model.addAttribute("trabajo", new Trabajo());
        return "trabajos-especificos";
    }
    @GetMapping("/{id}/detalle")
    public String DetallesxId(@PathVariable long id, Model model) {
        model.addAttribute("trabajo", customTrabajoDetailsService.obtenerPorId(id));
        return "trabajos/detalle";
    }

    @GetMapping("/{id}/miembros")
    public String mostrarMiembros(@PathVariable long id, Model model) {
        Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(id);
        model.addAttribute("trabajo", trabajo);
        model.addAttribute("miembros", trabajo.getColaboradores());
        model.addAttribute("totalMiembros", trabajo.getColaboradores().size());
        return "trabajos/miembros";
    }

    // Endpoint REST para obtener miembros con roles (JSON)
    @GetMapping("/{id}/miembros/roles")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerMiembrosConRoles(@PathVariable long id) {
        try {
            Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(id);

            if (trabajo == null) {
                return ResponseEntity.notFound().build();
            }

            List<User> colaboradores = new ArrayList<>(trabajo.getColaboradores());
            List<Map<String, Object>> miembros = new ArrayList<>();

            for (int i = 0; i < colaboradores.size(); i++) {
                User user = colaboradores.get(i);
                String rol = (i == 0) ? "LIDER" : "COLABORADOR";

                Map<String, Object> miembro = new HashMap<>();
                miembro.put("id", user.getId());
                miembro.put("username", user.getUsername());
                miembro.put("rol", rol);
                miembro.put("trabajosCount", user.getTrabajos().size());
                miembro.put("posicion", i + 1);

                miembros.add(miembro);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("trabajoId", trabajo.getId());
            response.put("nombreTrabajo", trabajo.getNombreTrabajo());
            response.put("totalMiembros", miembros.size());
            response.put("totalTareas", trabajo.getTareas().size());
            response.put("miembros", miembros);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
