package com.example.entregaya.controller;


import com.example.entregaya.model.Trabajo;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import com.example.entregaya.service.CustomTareaDetailsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/trabajos")
public class TrabajoController {

    private final CustomTrabajoDetailsService customTrabajoDetailsService;
    private final CustomTareaDetailsService customTareaDetailsService;

    public TrabajoController(CustomTrabajoDetailsService customTrabajoDetailsService,
                             CustomTareaDetailsService customTareaDetailsService) {
        this.customTrabajoDetailsService = customTrabajoDetailsService;
        this.customTareaDetailsService = customTareaDetailsService;
    }
    @GetMapping
    public String trabajo(Model model, @AuthenticationPrincipal UserDetails user) {
        List<Trabajo>trabajos= customTrabajoDetailsService.listarPorUsuario(user.getUsername());
        Map<Long,Integer> progresos = new HashMap<>();
        for(Trabajo t : trabajos){
            progresos.put(t.getId(), customTareaDetailsService.calcularProgreso(t.getId()));
        }

        model.addAttribute("trabajos", trabajos);
        model.addAttribute("progressos", progresos);
        return "trabajo/lista";
    }
    @GetMapping("/nuevo")
    public String formulario(Model model) {
        model.addAttribute("trabajo", new Trabajo());
        return "trabajo/formulario";
    }
    @PostMapping("/nuevo")
    public String guardar(@ModelAttribute Trabajo trabajo, @AuthenticationPrincipal UserDetails user) {
        customTrabajoDetailsService.crearTrabajo(trabajo,user.getUsername());
        return "redirect:/trabajos";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable long id, Model model) {
        model.addAttribute("trabajo", customTrabajoDetailsService.obtenerPorId(id));
        model.addAttribute("progreso", customTareaDetailsService.calcularProgreso(id));
        return "trabajo/detalle";
    }
    @PostMapping("/{id}/eliminar")
    public String eliminar (@PathVariable long id) {
        customTrabajoDetailsService.eliminar(id);
        return "redirect:/trabajos";
    }
}
