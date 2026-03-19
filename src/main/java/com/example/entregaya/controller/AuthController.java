package com.example.entregaya.controller;

import com.example.entregaya.model.Trabajo;
import com.example.entregaya.service.CustomInvitacionDetailsService;
import com.example.entregaya.service.CustomTareaDetailsService;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import com.example.entregaya.service.CustomUserDetailsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;


import java.util.*;

/**
 * Controlador encargado de manejar las vistas
 * relacionadas con autenticación.
 */
@Controller
public class AuthController {


    private final CustomUserDetailsService userDetailsService;
    private final CustomTareaDetailsService customTareaDetailsService;
    private final CustomTrabajoDetailsService customTrabajoDetailsService;
    private final CustomInvitacionDetailsService customInvitacionDetailsService;

    public AuthController(CustomUserDetailsService UserDetailsService,
                          CustomTrabajoDetailsService customTrabajoDetailsService,
                          CustomTareaDetailsService customTareaDetailsService, CustomInvitacionDetailsService customInvitacionDetailsService) {

        this.userDetailsService = UserDetailsService;
        this.customTareaDetailsService = customTareaDetailsService;
        this.customTrabajoDetailsService = customTrabajoDetailsService;
        this.customInvitacionDetailsService = customInvitacionDetailsService;
    }

    /**
     * Muestra la pagina de login personalizada.
     */
    @GetMapping("/login")
    public String login() {
        return "login"; //retorna login.html
    }

    /**
     * Muestra el formulario de registro.
     */
    @GetMapping("/register")
    public String registerForm() {
        return "register"; // retorna register.html
    }
    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           RedirectAttributes redirectAttributes) {
        try {
            userDetailsService.register(username, password);
            redirectAttributes.addFlashAttribute("success", "Registro Completado. Inicia sesión");
            return "redirect:/login";
        }catch(IllegalArgumentException il){
            redirectAttributes.addFlashAttribute("error", il.getMessage());
            return "redirect:/register";
        }
    }



    /**
     * Pagina principal luego de autenticacion.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<Trabajo> todos = customTrabajoDetailsService.listarPorUsuario(userDetails.getUsername());

        Map<Long, Integer> progresos = new HashMap<>();
        for (Trabajo trabajo : todos) {
            progresos.put(trabajo.getId(), customTareaDetailsService.calcularProgreso(trabajo.getId()));
        }
        List<Trabajo> activos=todos.stream()
                .filter(trabajo -> progresos.get(trabajo.getId()) <100)
                .toList();

        // Calcular trabajos próximos a vencer (en los próximos 7 días)
        List<Trabajo> proximosVencer = activos.stream()
                .filter(trabajo -> trabajo.getFechaEntrega() != null)
                .filter(trabajo -> {
                    java.time.LocalDate fechaEntrega = trabajo.getFechaEntrega().toLocalDate();
                    java.time.LocalDate ahora = java.time.LocalDate.now();
                    java.time.LocalDate limite = ahora.plusDays(7);
                    return !fechaEntrega.isBefore(ahora) && !fechaEntrega.isAfter(limite);
                })
                .toList();

        // HU-11: Contar tareas vencidas del usuario
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        long tareasVencidasCount = todos.stream()
                .flatMap(trabajo -> customTareaDetailsService.tareas(trabajo.getId()).stream())
                .filter(tarea -> !tarea.getIsCompletada() && 
                               tarea.getFechaFinal() != null && 
                               tarea.getFechaFinal().isBefore(ahora))
                .count();

        model.addAttribute("trabajos", activos);
        model.addAttribute("progresos", progresos);
        model.addAttribute("totalTrabajos", todos.size());
        model.addAttribute("completados", todos.size()-activos.size());
        model.addAttribute("proximosVencer", proximosVencer);
        model.addAttribute("invitaciones", customInvitacionDetailsService.pendientesParaUsuario(userDetails.getUsername()));
        // HU-11: Agregar contador de tareas vencidas
        model.addAttribute("tareasVencidas", tareasVencidasCount);
        return "dashboard";
    }
}
