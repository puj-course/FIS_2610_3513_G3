package com.example.entregaya.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador encargado de manejar las vistas
 * relacionadas con autenticación.
 */
@Controller
public class AuthController {

    /**
     * Muestra la pagina de login personalizada.
     */
    @GetMapping("/login")
    public String login() {
        return "login"; //retorna login.html
    }

    /**
     * Pagina principal luego de autenticacion.
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
