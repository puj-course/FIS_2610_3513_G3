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
                           CustomComentarioDetailsService customComentarioDetailsService, UserRepository userRepository, Lideroeditorstrategy lideroeditorstrategy) {
        this.tareaRepository = tareaRepository;
        this.customTareaDetailsService = customTareaDetailsService;
        this.customTrabajoDetailsService = customTrabajoDetailsService;
        this.customComentarioDetailsService = customComentarioDetailsService;
        this.userRepository = userRepository;
        this.lideroeditorstrategy = lideroeditorstrategy;
    }

    @GetMapping("/CrearTarea")
    public String formulario(Model model, @PathVariable long trabajoId) {
        Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(trabajoId);
        model.addAttribute("tarea", new Tarea());
        model.addAttribute("trabajoId", trabajoId);
        model.addAttribute("dificultades", Tarea.Dificultad.values());
        model.addAttribute("colaboradores", trabajo.getColaboradores());
        return "trabajos/tareas/CrearTarea";
    }

    /**
     * HU-22 (#271): Crear nueva tarea con validaciones del Builder.
     * Captura IllegalStateException del TareaBuilder y muestra error en formulario.
     * 
     * @param trabajoId ID del trabajo al que pertenece la tarea
     * @param tarea Datos del formulario
     * @param responsableIds IDs de responsables seleccionados
     * @param redirectAttributes Para mensajes flash
     * @return Redirect a lista si éxito, redirect a formulario si error
     */
    @PostMapping("/nueva")
    public String guardar(@PathVariable long trabajoId, 
                         @ModelAttribute Tarea tarea, 
                         @RequestParam(value = "responsableIds", required = false) List<Long> responsableIds,
                         RedirectAttributes redirectAttributes) {
        try {
            // Intentar crear la tarea (TareaBuilder valida en build())
            customTareaDetailsService.crearTarea(tarea, trabajoId, responsableIds);
            
            // Si llegamos aquí, la tarea se creó exitosamente
            redirectAttributes.addFlashAttribute("success", "Tarea creada correctamente.");
            return "redirect:/trabajos/" + trabajoId;
            
        } catch (IllegalStateException e) {
            // Error de validación del Builder (nombre vacío o fechas inconsistentes)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            
            // Preservar los datos del formulario para que el usuario no tenga que reescribir todo
            redirectAttributes.addFlashAttribute("tarea", tarea);
            if (responsableIds != null && !responsableIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("responsableIdsSeleccionados", responsableIds);
            }
            
            // Redirigir de vuelta al formulario (NO a la lista)
            return "redirect:/trabajos/" + trabajoId + "/tareas/CrearTarea";
            
        } catch (Exception e) {
            // Error inesperado
            redirectAttributes.addFlashAttribute("error", "Error al crear la tarea: " + e.getMessage());
            return "redirect:/trabajos/" + trabajoId + "/tareas/CrearTarea";
        }
    }

    @PostMapping("/{tareaId}/eliminar")
    public String eliminar(@PathVariable Long trabajoId, @PathVariable Long tareaId) {
        customTareaDetailsService.eliminar(tareaId);
        return "redirect:/trabajos/" + trabajoId;
    }

    @PostMapping("/{tareaId}/completar")
    public String completar(@PathVariable Long trabajoId, @PathVariable Long tareaId) {
        customTareaDetailsService.toggleCompletada(tareaId);
        return "redirect:/trabajos/" + trabajoId;
    }

    @PostMapping("/{tareaId}/asignar")
    public String asignarResponsables(@PathVariable Long trabajoId, @PathVariable Long tareaId, @RequestParam(value = "responsableIds", required = false) List<Long> responsableIds) {
        customTareaDetailsService.actualizarResponsables(tareaId, responsableIds);
        return "redirect:/trabajos/" + trabajoId;
    }

    @GetMapping("/{tareaId}/editar")
    public String formularioEditar(@PathVariable long trabajoId, @PathVariable Long tareaId,
                                   Model model, @AuthenticationPrincipal UserDetails user) {
        // Verificar que el usuario tenga permiso (LIDER o EDITOR)
        if (!customTrabajoDetailsService.verificarPermiso(trabajoId,user.getUsername(), lideroeditorstrategy)) {
            return "redirect:/trabajos/" + trabajoId + "?error=noPermiso";
        }

        Tarea tarea = customTareaDetailsService.findById(tareaId);
        Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(trabajoId);

        model.addAttribute("tarea", tarea);
        model.addAttribute("trabajoId", trabajoId);
        model.addAttribute("dificultades", Tarea.Dificultad.values());
        model.addAttribute("colaboradores", trabajo.getColaboradores());
        model.addAttribute("responsablesSeleccionados", tarea.getResponsables().stream()
                .map(User::getId).toList());

        return "trabajos/tareas/EditarTarea";
    }
    /**
     * HU-22 (#271): Editar tarea existente con validaciones del Builder.
     * Captura IllegalStateException del TareaBuilder y muestra error en formulario.
     * 
     * @param trabajoId ID del trabajo
     * @param tareaId ID de la tarea a editar
     * @param tarea Datos actualizados del formulario
     * @param responsableIds IDs de responsables seleccionados
     * @param user Usuario autenticado
     * @param redirectAttributes Para mensajes flash
     * @return Redirect a lista si éxito, redirect a formulario si error
     */
    @PostMapping("/{tareaId}/editar")
    public String guardarEdicion(@PathVariable long trabajoId, 
                                @PathVariable Long tareaId,
                                @ModelAttribute Tarea tarea,
                                @RequestParam(value = "responsableIds", required = false) List<Long> responsableIds,
                                @AuthenticationPrincipal UserDetails user,
                                RedirectAttributes redirectAttributes) {

        // Verificar permisos (LIDER o EDITOR)
        if (!customTrabajoDetailsService.verificarPermiso(trabajoId, user.getUsername(), lideroeditorstrategy)) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar tareas.");
            return "redirect:/trabajos/" + trabajoId;
        }

        try {
            // Intentar editar la tarea (TareaBuilder valida en build())
            customTareaDetailsService.editarTarea(tareaId, tarea, responsableIds);
            
            // Si llegamos aquí, la tarea se editó exitosamente
            redirectAttributes.addFlashAttribute("success", "Tarea actualizada correctamente.");
            return "redirect:/trabajos/" + trabajoId;
            
        } catch (IllegalStateException e) {
            // Error de validación del Builder (nombre vacío o fechas inconsistentes)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            
            // Preservar los datos del formulario
            redirectAttributes.addFlashAttribute("tarea", tarea);
            if (responsableIds != null && !responsableIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("responsableIdsSeleccionados", responsableIds);
            }
            
            // Redirigir de vuelta al formulario de edición (NO a la lista)
            return "redirect:/trabajos/" + trabajoId + "/tareas/" + tareaId + "/editar";
            
        } catch (Exception e) {
            // Error inesperado
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la tarea: " + e.getMessage());
            return "redirect:/trabajos/" + trabajoId + "/tareas/" + tareaId + "/editar";
        }
    }

    @PostMapping("/{tareaId}/comentario")
    public String agregarComentario(@PathVariable Long trabajoId,
                                    @PathVariable Long tareaId,
                                    @RequestParam String contenido,
                                    @AuthenticationPrincipal UserDetails userDetails) {

        // Obtener el usuario autenticado
        User usuario = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear el comentario
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



}
