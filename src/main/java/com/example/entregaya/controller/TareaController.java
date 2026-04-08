package com.example.entregaya.controller;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.ColaboradorTrabajo;
import com.example.entregaya.service.CustomTareaDetailsService;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.repository.UserRepository;
import com.example.entregaya.strategy.Lideroeditorstrategy;
import com.example.entregaya.strategy.Sololiderstrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para gestionar las tareas dentro de un trabajo.
 * HU-20, HU-21, HU-22 — Gestión completa de tareas con validación y permisos.
 */
@Controller
@RequestMapping("/trabajos")
public class TareaController {

    @Autowired
    private CustomTareaDetailsService customTareaDetailsService;

    @Autowired
    private CustomTrabajoDetailsService customTrabajoDetailsService;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Lideroeditorstrategy lideroeditorstrategy;

    @Autowired
    private Sololiderstrategy sololiderstrategy;

    /**
     * Mostrar formulario de creación de tarea
     */
    @GetMapping("/{id}/tareas/CrearTarea")
    public String mostrarFormularioCrearTarea(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {

        try {
            Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(id);
            model.addAttribute("trabajo", trabajo);
            model.addAttribute("tarea", new Tarea());
            return "trabajos/tareas/CrearTarea";
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo cargar el formulario de creación de tarea");
            return "trabajos/tareas/CrearTarea";
        }
    }

    /**
     * Crear una nueva tarea en un trabajo
     */
    @PostMapping("/{id}/tareas/nueva")
    public String crearTarea(
            @PathVariable Long id,
            @ModelAttribute Tarea tarea,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        try {
            // Validar que el usuario tenga permisos (solo LIDER puede crear tareas)
            String username = authentication.getName();
            boolean tienePermiso = customTrabajoDetailsService.verificarPermiso(id, username, sololiderstrategy);

            if (!tienePermiso) {
                redirectAttributes.addFlashAttribute("error", "No tienes permisos para crear tareas en este trabajo");
                return "redirect:/trabajos/" + id + "/tareas/CrearTarea";
            }

            // Crear la tarea sin responsables (se pueden asignar después)
            Tarea tareaCreada = customTareaDetailsService.crearTarea(tarea, id);
            redirectAttributes.addFlashAttribute("success", "Tarea creada exitosamente");
            return "redirect:/trabajos/" + id;

        } catch (IllegalStateException e) {
            // Capturar errores de validación del Builder
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/trabajos/" + id + "/tareas/CrearTarea";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la tarea: " + e.getMessage());
            return "redirect:/trabajos/" + id + "/tareas/CrearTarea";
        }
    }

    /**
     * Mostrar formulario de edición de tarea
     */
    @GetMapping("/{id}/tareas/{tareaId}/editar")
    public String mostrarFormularioEditarTarea(
            @PathVariable Long id,
            @PathVariable Long tareaId,
            Model model,
            Authentication authentication) {

        try {
            Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(id);
            Tarea tarea = customTareaDetailsService.findById(tareaId);

            model.addAttribute("trabajo", trabajo);
            model.addAttribute("tarea", tarea);
            return "trabajos/tareas/EditarTarea";
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo cargar el formulario de edición");
            return "trabajos/tareas/EditarTarea";
        }
    }

    /**
     * Editar una tarea existente
     */
    @PostMapping("/{id}/tareas/{tareaId}/editar")
    public String editarTarea(
            @PathVariable Long id,
            @PathVariable Long tareaId,
            @ModelAttribute Tarea tarea,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        try {
            // Verificar permisos (EDITOR o LIDER pueden editar)
            String username = authentication.getName();
            boolean tienePermiso = customTrabajoDetailsService.verificarPermiso(id, username, lideroeditorstrategy);

            if (!tienePermiso) {
                redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar tareas");
                return "redirect:/trabajos/" + id + "/tareas/" + tareaId + "/editar";
            }

            // Editar la tarea sin cambiar responsables (se pueden editar en otra acción)
            Tarea tareaActualizada = customTareaDetailsService.editarTarea(tareaId, tarea, null);
            redirectAttributes.addFlashAttribute("success", "Tarea actualizada exitosamente");
            return "redirect:/trabajos/" + id;

        } catch (IllegalStateException e) {
            // Capturar errores de validación del Builder
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/trabajos/" + id + "/tareas/" + tareaId + "/editar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al editar la tarea: " + e.getMessage());
            return "redirect:/trabajos/" + id + "/tareas/" + tareaId + "/editar";
        }
    }

    /**
     * Ver detalle de una tarea
     */
    @GetMapping("/{id}/tareas/{tareaId}/detalle")
    public String verDetalleTarea(
            @PathVariable Long id,
            @PathVariable Long tareaId,
            Model model,
            Authentication authentication) {

        try {
            Trabajo trabajo = customTrabajoDetailsService.obtenerPorId(id);
            Tarea tarea = customTareaDetailsService.findById(tareaId);

            model.addAttribute("trabajo", trabajo);
            model.addAttribute("tarea", tarea);
            return "trabajos/tareas/detalle_tarea";
        } catch (Exception e) {
            model.addAttribute("error", "Tarea no encontrada");
            return "error";
        }
    }

    /**
     * Eliminar una tarea
     */
    @PostMapping("/{id}/tareas/{tareaId}/eliminar")
    public String eliminarTarea(
            @PathVariable Long id,
            @PathVariable Long tareaId,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            boolean tienePermiso = customTrabajoDetailsService.verificarPermiso(id, username, sololiderstrategy);

            if (!tienePermiso) {
                redirectAttributes.addFlashAttribute("error", "No tienes permisos para eliminar tareas");
                return "redirect:/trabajos/" + id;
            }

            customTareaDetailsService.eliminar(tareaId);
            redirectAttributes.addFlashAttribute("success", "Tarea eliminada exitosamente");
            return "redirect:/trabajos/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la tarea");
            return "redirect:/trabajos/" + id;
        }
    }
}
