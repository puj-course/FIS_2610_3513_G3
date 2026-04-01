package com.example.entregaya.controller;

import com.example.entregaya.dto.DashboardDTO;
import com.example.entregaya.facade.DashboardFacade;
import com.example.entregaya.service.CustomUserDetailsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

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
                           @RequestParam("email") String email,
                           RedirectAttributes redirectAttributes) {
        try {
            userDetailsService.register(username, password, email);
            redirectAttributes.addFlashAttribute("success", "Registro Completado. Inicia sesión");
            return "redirect:/login";
        }catch(IllegalArgumentException il){
            redirectAttributes.addFlashAttribute("error", il.getMessage());
            return "redirect:/register";
        }
    }



    /**
     * Pagina principal luego de autenticacion.
     * La lógica de cálculo fue extraída a {@link com.example.entregaya.facade.DashboardFacade}.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        DashboardDTO data = dashboardFacade.getDashboardData(userDetails.getUsername());

        model.addAttribute("trabajos",      data.getTrabajos());
        model.addAttribute("progresos",     data.getProgresos());
        model.addAttribute("totalTrabajos", data.getTotalTrabajos());
        model.addAttribute("completados",   data.getCompletados());
        model.addAttribute("proximosVencer",data.getProximosVencer());
        model.addAttribute("invitaciones",  data.getInvitaciones());
        model.addAttribute("tareasVencidas",data.getTareasVencidas());
        return "dashboard";
    }
}