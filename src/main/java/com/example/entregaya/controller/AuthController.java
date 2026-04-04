package com.example.entregaya.controller;

import com.example.entregaya.facade.DashboardFacade;
import com.example.entregaya.service.CustomUserDetailsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador encargado de manejar las vistas
 * relacionadas con autenticación.
 */
@Controller
public class AuthController {

    private final CustomUserDetailsService userDetailsService;
    private final DashboardFacade dashboardFacade;

    public AuthController(CustomUserDetailsService userDetailsService,
                          DashboardFacade dashboardFacade) {
        this.userDetailsService = userDetailsService;
        this.dashboardFacade = dashboardFacade;
    }

    /** Muestra la página de login personalizada. */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /** Muestra el formulario de registro. */
    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("email")    String email,
                           RedirectAttributes redirectAttributes) {
        try {
            userDetailsService.register(username, password, email);
            redirectAttributes.addFlashAttribute("success", "Registro Completado. Inicia sesión");
            return "redirect:/login";
        } catch (IllegalArgumentException il) {
            redirectAttributes.addFlashAttribute("error", il.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * Página principal luego de autenticación.
     * Toda la lógica de cálculo vive en {@link DashboardFacade}.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("data", dashboardFacade.getDashboardData(userDetails.getUsername()));
        return "dashboard";
    }
}
