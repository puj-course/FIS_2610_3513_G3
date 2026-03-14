package com.example.entregaya.controller;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.service.CustomInvitacionDetailsService;
import com.example.entregaya.service.CustomTareaDetailsService;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/trabajos/{trabajoId}/tareas")
public class TareaController {
    private final TareaRepository tareaRepository;
    private final CustomTareaDetailsService customTareaDetailsService;
    private final CustomTrabajoDetailsService customTrabajoDetailsService;

    public TareaController(TareaRepository tareaRepository, CustomTareaDetailsService customTareaDetailsService, CustomTrabajoDetailsService customTrabajoDetailsService) {
        this.tareaRepository = tareaRepository;
        this.customTareaDetailsService = customTareaDetailsService;
        this.customTrabajoDetailsService = customTrabajoDetailsService;
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
    @PostMapping("/nueva")
    public String guardar(@PathVariable long trabajoId, @ModelAttribute Tarea tarea, @RequestParam(value= "responsableIds",required = false) List<Long> responsableIds) {
        customTareaDetailsService.crearTarea(tarea, trabajoId,responsableIds);
        return "redirect:/trabajos/" + trabajoId;
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
    public String asignarResponsables(@PathVariable Long trabajoId, @PathVariable Long tareaId, @RequestParam(value="responsableIds",required = false) List<Long> responsableIds) {
        customTareaDetailsService.actualizarResponsables(tareaId,responsableIds);
        return "redirect:/trabajos/" + trabajoId;
    }
}
