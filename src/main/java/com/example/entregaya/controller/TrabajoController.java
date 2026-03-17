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

        // HU-11: Lógica de comparación de fechas para alertas visuales
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.LocalDateTime en24Horas = ahora.plusHours(24);
        
        // Identificar tareas vencidas (fechaFinal < ahora && !completada)
        List<Long> tareasVencidas = tareas.stream()
                .filter(t -> !t.getIsCompletada() && 
                           t.getFechaFinal() != null && 
                           t.getFechaFinal().isBefore(ahora))
                .map(Tarea::getId)
                .toList();
        
        // Identificar tareas que vencen pronto (fechaFinal entre ahora y ahora+24h && !completada)
        List<Long> tareasVencenPronto = tareas.stream()
                .filter(t -> !t.getIsCompletada() && 
                           t.getFechaFinal() != null && 
                           t.getFechaFinal().isAfter(ahora) &&
                           t.getFechaFinal().isBefore(en24Horas))
                .map(Tarea::getId)
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
        // HU-11: Agregar listas de alertas al modelo
        model.addAttribute("tareasVencidas", tareasVencidas);
        model.addAttribute("tareasVencenPronto", tareasVencenPronto);
        return "trabajos/detalle";
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
        
        // Convertir Set a List y ordenar: LIDER primero, luego por username
        List<ColaboradorTrabajo> miembrosOrdenados = new ArrayList<>(trabajo.getColaboradores());
        miembrosOrdenados.sort((m1, m2) -> {
            // LIDER siempre primero
            if (m1.getRol() == ColaboradorTrabajo.Rol.LIDER) return -1;
            if (m2.getRol() == ColaboradorTrabajo.Rol.LIDER) return 1;
            // Si ambos son COLABORADOR, ordenar por username
            return m1.getUser().getUsername().compareTo(m2.getUser().getUsername());
        });
        
        model.addAttribute("trabajo", trabajo);
        model.addAttribute("miembros", miembrosOrdenados);
        model.addAttribute("totalMiembros", miembrosOrdenados.size());
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

            List<ColaboradorTrabajo> colaboradores = new ArrayList<>(trabajo.getColaboradores());
            List<Map<String, Object>> miembros = new ArrayList<>();
            
            for (int i = 0; i < colaboradores.size(); i++) {
                ColaboradorTrabajo colaborador = colaboradores.get(i);
                User user = colaborador.getUser();
                
                // Obtener el rol real desde la entidad ColaboradorTrabajo
                String rol = colaborador.getRol().name(); // LIDER, EDITOR, o COLABORADOR
                
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
