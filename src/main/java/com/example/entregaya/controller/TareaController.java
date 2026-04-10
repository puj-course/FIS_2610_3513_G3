package com.example.entregaya.controller;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.repository.UserRepository;
import com.example.entregaya.service.CustomComentarioDetailsService;
import com.example.entregaya.service.CustomInvitacionDetailsService;
import com.example.entregaya.service.CustomTareaDetailsService;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import com.example.entregaya.strategy.Lideroeditorstrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/trabajos/{trabajoId}/tareas")
public class TareaController {
    private final TareaRepository tareaRepository;
    private final CustomTareaDetailsService customTareaDetailsService;
    private final CustomTrabajoDetailsService customTrabajoDetailsService;
    private final CustomComentarioDetailsService customComentarioDetailsService;
    private final UserRepository userRepository;
    private final Lideroeditorstrategy lideroeditorstrategy;

    public TareaController(TareaRepository tareaRepository,
                           CustomTareaDetailsService customTareaDetailsService,
                           CustomTrabajoDetailsService customTrabajoDetailsService,
                           CustomComentarioDetailsService customComentarioDetailsService,
                           UserRepository userRepository,
                           Lideroeditorstrategy lideroeditorstrategy) {
        this.tareaRepository = tareaRepository;
        this.customTareaDetailsService = customTareaDetailsService;
        this.customTrabajoDetailsService = customTrabajoDetailsService;
        this.customComentarioDetailsService = customComentarioDetailsService;
        this.userRepository = userRepository;
        this.lideroeditorstrategy = lideroeditorstrategy;
    }

    @GetMapping("/nuevo")
    public String formulario(@PathVariable Long trabajoId, Model model) {
        Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(trabajoId);
        model.addAttribute("tarea", new Tarea());
        model.addAttribute("trabajoId", trabajoId);
        model.addAttribute("dificultades", Tarea.Dificultad.values());
        model.addAttribute("colaboradores", trabajo.getColaboradores());
        return "trabajos/tareas/CrearTarea";
    }

    @PostMapping("/nueva")
    public String guardar(@PathVariable Long trabajoId,
                          @ModelAttribute Tarea tarea,
                          @RequestParam(value = "responsableIds", required = false) List<Long> responsableIds) {
        customTareaDetailsService.crearTarea(tarea, trabajoId, responsableIds);
        return "redirect:/trabajos/" + trabajoId;
    }

    @PostMapping("/{tareaId}/eliminar")
    public String eliminar(@PathVariable Long trabajoId, @PathVariable Long tareaId) {
        customTareaDetailsService.eliminar(tareaId);
        return "redirect:/trabajos/" + trabajoId;
    }

    /**
     * Pasa el username del usuario autenticado para que el Observer
     * pueda incluirlo en la notificación (CA2 HU-3).
     */
    @PostMapping("/{tareaId}/completar")
    public String completar(@PathVariable Long trabajoId,
                            @PathVariable Long tareaId,
                            @AuthenticationPrincipal UserDetails userDetails) {
        customTareaDetailsService.toggleCompletada(tareaId, userDetails.getUsername());
        return "redirect:/trabajos/" + trabajoId;
    }

    @PostMapping("/{tareaId}/asignar")
    public String asignarResponsables(@PathVariable Long trabajoId,
                                      @PathVariable Long tareaId,
                                      @RequestParam(value = "responsableIds", required = false) List<Long> responsableIds) {
        customTareaDetailsService.actualizarResponsables(tareaId, responsableIds);
        return "redirect:/trabajos/" + trabajoId;
    }

    @GetMapping("/{tareaId}/editar")
    public String formularioEditar(@PathVariable Long trabajoId,
                                   @PathVariable Long tareaId,
                                   Model model,
                                   @AuthenticationPrincipal UserDetails user) {
        if (!customTrabajoDetailsService.verificarPermiso(trabajoId, user.getUsername(), lideroeditorstrategy)) {
            return "redirect:/trabajos/" + trabajoId + "?error=noPermiso";
        }

        Tarea tarea = customTareaDetailsService.findById(tareaId);
        Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(trabajoId);

        model.addAttribute("tarea", tarea);
        model.addAttribute("trabajoId", trabajoId);
        model.addAttribute("dificultades", Tarea.Dificultad.values());
        model.addAttribute("colaboradores", trabajo.getColaboradores());
        model.addAttribute("responsablesSeleccionados",
                tarea.getResponsables().stream().map(User::getId).toList());

        return "trabajos/tareas/EditarTarea";
    }

    @PostMapping("/{tareaId}/editar")
    public String guardarEdicion(@PathVariable Long trabajoId,
                                 @PathVariable Long tareaId,
                                 @ModelAttribute Tarea tarea,
                                 @RequestParam(value = "responsableIds", required = false) List<Long> responsableIds,
                                 @AuthenticationPrincipal UserDetails user) {
        if (!customTrabajoDetailsService.verificarPermiso(trabajoId, user.getUsername(), lideroeditorstrategy)) {
            return "redirect:/trabajos/" + trabajoId + "?error=noPermiso";
        }

        customTareaDetailsService.editarTarea(tareaId, tarea, responsableIds);
        return "redirect:/trabajos/" + trabajoId;
    }

    @PostMapping("/{tareaId}/comentario")
    public String agregarComentario(@PathVariable Long trabajoId,
                                    @PathVariable Long tareaId,
                                    @RequestParam String contenido,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        User usuario = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        customComentarioDetailsService.crearComentario(tareaId, usuario.getId(), contenido);

        return "redirect:/trabajos/" + trabajoId + "/tareas/" + tareaId + "/detalle";
    }

    @GetMapping("/{tareaId}/detalle")
    public String verDetalleTarea(@PathVariable Long trabajoId,
                                  @PathVariable Long tareaId,
                                  Model model) {
        Tarea tarea = customTareaDetailsService.findById(tareaId);

        model.addAttribute("tarea", tarea);
        model.addAttribute("trabajoId", trabajoId);

        return "trabajos/tareas/detalle_tarea";
    }

    /**
     * Clona la tarea indicada dentro del mismo trabajo.
     * Retorna 201 Created con la tarea clonada, o 403 si no tiene permiso.
     */
    @PostMapping("/{tareaId}/clonar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clonarTarea(
            @PathVariable Long trabajoId,
            @PathVariable Long tareaId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Tarea clon = customTareaDetailsService.clonarTarea(tareaId, trabajoId, userDetails.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("id", clon.getId());
            response.put("nombre", clon.getNombre());
            response.put("descripcion", clon.getDescripcion());
            response.put("dificultad", clon.getDificultad().name());

            return ResponseEntity.status(201).body(response);

        } catch (SecurityException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }

}
