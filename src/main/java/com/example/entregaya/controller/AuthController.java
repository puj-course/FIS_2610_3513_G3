package com.example.entregaya.controller;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.service.CustomUserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador encargado de manejar las vistas
 * relacionadas con autenticación.
 */
@Controller
public class AuthController {


    private final CustomUserDetailsService userDetailsService;
    private final TareaRepository tareaRepository;

    public AuthController(CustomUserDetailsService UserDetailsService, TareaRepository tareaRepository) {
        this.userDetailsService = UserDetailsService;
        this.tareaRepository = tareaRepository;
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
    public String dashboard(@RequestParam(name = "id", required = false) Long id) {
        long startTime = System.currentTimeMillis();
        List<Tarea> listaTareas = tareaRepository.buscarPorTrabajoNativo(id);
        long backendDuration = System.currentTimeMillis() - startTime;
        System.out.println("Timepo: " + backendDuration + "ms");

        return "dashboard"; // Retorna dashboard.html
    }
}
