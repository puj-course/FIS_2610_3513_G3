package com.example.entregaya.controller;

import com.example.entregaya.dto.TareaConEtiquetaDTO;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.repository.UserRepository;
import com.example.entregaya.service.CustomComentarioDetailsService;
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
                          @RequestParam(value = "responsableIds", required = false) List<Long> responsableIds,
                          @RequestParam(value = "etiquetas", required = false) List<String> etiquetas,
                          RedirectAttributes redirectAttributes) {
        try {
            customTareaDetailsService.crearTarea(tarea, trabajoId, responsableIds, etiquetas);
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/trabajos/" + trabajoId + "/tareas/nuevo";
        }
        return "redirect:/trabajos/" + trabajoId;
    }

    @PostMapping("/{tareaId}/eliminar")
    public String eliminar(@PathVariable Long trabajoId, @PathVariable Long tareaId) {
        customTareaDetailsService.eliminar(tareaId);
        return "redirect:/trabajos/" + trabajoId;
    }

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
        model.addAttribute("etiquetasExistentes",
                String.join(",", tarea.getEtiquetas()));
        return "trabajos/tareas/EditarTarea";
    }

    @PostMapping("/{tareaId}/editar")
    public String guardarEdicion(@PathVariable Long trabajoId,
                                 @PathVariable Long tareaId,
                                 @ModelAttribute Tarea tarea,
                                 @RequestParam(value = "responsableIds", required = false) List<Long> responsableIds,
                                 @RequestParam(value = "etiquetas", required = false) List<String> etiquetas,
                                 @AuthenticationPrincipal UserDetails user,
                                 RedirectAttributes redirectAttributes) {
        if (!customTrabajoDetailsService.verificarPermiso(trabajoId, user.getUsername(), lideroeditorstrategy)) {
            return "redirect:/trabajos/" + trabajoId + "?error=noPermiso";
        }
        try {
            customTareaDetailsService.editarTarea(tareaId, tarea, responsableIds, etiquetas);
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/trabajos/" + trabajoId + "/tareas/" + tareaId + "/editar";
        }
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

    /**
     * HU-45 (#445): Editar un comentario propio.
     * Verifica autoría en el servicio; devuelve 403 si no es el autor.
     */
    @PutMapping("/{tareaId}/comentarios/{comentarioId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> editarComentario(
            @PathVariable Long trabajoId,
            @PathVariable Long tareaId,
            @PathVariable Long comentarioId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        String nuevoContenido = body.get("contenido");
        if (nuevoContenido == null || nuevoContenido.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El contenido no puede estar vacío."));
        }

        try {
            customComentarioDetailsService.editarComentario(
                    comentarioId, nuevoContenido, userDetails.getUsername());
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Comentario actualizado correctamente.",
                    "contenido", nuevoContenido));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * HU-45 (#446): Eliminar un comentario propio.
     * Verifica autoría en el servicio; devuelve 403 si no es el autor.
     */
    @DeleteMapping("/{tareaId}/comentarios/{comentarioId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarComentario(
            @PathVariable Long trabajoId,
            @PathVariable Long tareaId,
            @PathVariable Long comentarioId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            customComentarioDetailsService.eliminarComentario(
                    comentarioId, userDetails.getUsername());
            return ResponseEntity.ok(Map.of("mensaje", "Comentario eliminado correctamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{tareaId}/detalle")
    public String verDetalleTarea(@PathVariable Long trabajoId,
                                  @PathVariable Long tareaId,
                                  Model model) {
        Tarea tarea = customTareaDetailsService.findById(tareaId);
        TareaConEtiquetaDTO tareaConEtiqueta = customTareaDetailsService.findByIdConEtiqueta(tareaId);

        model.addAttribute("tarea", tarea);
        model.addAttribute("tareaConEtiqueta", tareaConEtiqueta);
        model.addAttribute("trabajoId", trabajoId);

        return "trabajos/tareas/detalle tarea";
    }

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
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }
}