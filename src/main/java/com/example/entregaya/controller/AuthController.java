package com.example.entregaya.controller;

import com.example.entregaya.service.CustomUserDetailsService;
import org.springframework.stereotype.Controller;
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

    public AuthController(CustomUserDetailsService UserDetailsService) {
        this.userDetailsService = UserDetailsService;
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
            return "redirect:/login";
        }
    }



    /**
     * Pagina principal luego de autenticacion.
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
