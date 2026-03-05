package com.example.entregaya.controller;


import com.example.entregaya.model.Trabajo;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/trabajos")
public class TrabajoController {

    private final CustomTrabajoDetailsService customTrabajoDetailsService;

    public TrabajoController(CustomTrabajoDetailsService customTrabajoDetailsService) {
        this.customTrabajoDetailsService = customTrabajoDetailsService;
    }
    @GetMapping
    public String trabajo(Model model, @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("trabajos", customTrabajoDetailsService.listarPorUsuario(user.getUsername()));
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
        return "trabajo/detalle";
    }
    @PostMapping("/{id}/eliminar")
    public String eliminar (@PathVariable long id) {
        customTrabajoDetailsService.obtenerPorId(id);
        return "redirect:/trabajos";
    }
}
