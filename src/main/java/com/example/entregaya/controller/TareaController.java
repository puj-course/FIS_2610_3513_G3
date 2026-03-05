package com.example.entregaya.controller;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.service.CustomTareaDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/trabajos/{trabajoId}/tareas")
public class TareaController {
    private final TareaRepository tareaRepository;
    private final CustomTareaDetailsService customTareaDetailsService;

    public TareaController(TareaRepository tareaRepository, CustomTareaDetailsService customTareaDetailsService) {
        this.tareaRepository = tareaRepository;
        this.customTareaDetailsService = customTareaDetailsService;
    }

    @GetMapping("/nueva")
    public String formulario(Model model, @PathVariable long trabajoId) {
        model.addAttribute("tarea", new Tarea());
        model.addAttribute("trabajoId", trabajoId);
        return "trabajos/tareas";
    }
    @PostMapping("/nueva")
    public String guardar(@PathVariable long trabajoId, @ModelAttribute Tarea tarea) {
        customTareaDetailsService.crearTarea(tarea, trabajoId);
        return "redirect:/trabajos/" + trabajoId;
    }

    @PostMapping("/{tareaId}/eliminar")
    public String eliminar(@PathVariable Long trabajoId,
                           @PathVariable Long tareaId) {
        customTareaDetailsService.eliminar(tareaId);
        return "redirect:/trabajos/" + trabajoId;
    }
}
